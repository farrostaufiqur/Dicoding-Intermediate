package com.belajar.storyapp.ui.maps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.belajar.storyapp.R
import com.belajar.storyapp.databinding.ActivityMapsBinding
import com.belajar.storyapp.ui.login.LoginActivity
import com.belajar.storyapp.ui.main.MainActivity
import com.belajar.storyapp.ui.settings.SettingsActivity
import com.belajar.storyapp.util.AppPreferences
import com.belajar.storyapp.util.PreferencesModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var _binding: ActivityMapsBinding? = null
    private val binding get() = _binding
    private var maps: Job = Job()

    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var viewModel: MapsViewModel
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        setupViewModel()
        getToken()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationProvider = LocationServices.getFusedLocationProviderClient(this)
        viewModel.isLoading.observe(this) {
            isLoading(it)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
            isMyLocationButtonEnabled = true
            setAllGesturesEnabled(true)
        }

        setMapStyle()
        getMyLocation()
        addManyMarker()
    }

    private fun getToken() {
        viewModel.getUserToken().observe(this){
            if (it != null){
                token = it.toString()
            }else{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
    }

    private fun setupViewModel() {
        val pref = AppPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, PreferencesModelFactory(pref))[MapsViewModel::class.java]
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            locationProvider.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                } else {
                    Toast.makeText(this, getString(R.string.not_permitted), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        viewModel.getIsDarkMode().observe(this){
            try {
                val success = if(it == true){
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.dark_mode_maps))
                }else{
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.light_mode_maps))
                }

                if (!success) {
                    Log.e(TAG, "Style parsing failed.")
                }

            } catch (exception: Resources.NotFoundException) {
                Log.e(TAG, "Can't find style. Error: ", exception)
            }
        }
    }

    private val boundsBuilder = LatLngBounds.Builder()

    private fun addManyMarker() {
        lifecycleScope.launchWhenResumed {
            if (maps.isActive) maps.cancel()
            maps = launch {
                viewModel.getAllStories(token.toString())
                viewModel.story.observe(this@MapsActivity) { it ->
                    it.forEach {
                        if(it.lon != null && it.lat != null) {
                            val latLng = LatLng(it.lat, it.lon)
                            val addressName = getAddressName(it.lat, it.lon)
                            mMap.addMarker(
                                MarkerOptions().position(latLng).title(it.name).snippet(addressName)
                            )
                            boundsBuilder.include(latLng)
                        }
                    }
                }
            }
        }
    }

    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)
                Log.d(TAG, "getAddress: $addressName")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_maps, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.story -> {
                super.onBackPressed()
                true
            }
            else -> false
        }
    }

    private fun isLoading(loading: Boolean) {
        binding?.apply {
            if (loading) {
                pbMaps.visibility = View.VISIBLE
            } else {
                pbMaps.visibility = View.INVISIBLE
            }
        }
    }

    companion object{
        const val TAG = "MapsActivity"
    }
}