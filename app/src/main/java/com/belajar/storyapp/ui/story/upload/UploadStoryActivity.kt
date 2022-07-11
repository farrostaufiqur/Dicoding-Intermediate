package com.belajar.storyapp.ui.story.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.belajar.storyapp.R
import com.belajar.storyapp.const.Constant.CAMERA_X
import com.belajar.storyapp.const.Constant.REQ_CODE_PERMIT
import com.belajar.storyapp.databinding.ActivityUploadStoryBinding
import com.belajar.storyapp.ui.camera.CameraActivity
import com.belajar.storyapp.ui.main.MainActivity
import com.belajar.storyapp.ui.main.MainActivity.Companion.EXTRA_TOKEN_MAIN
import com.belajar.storyapp.util.reduceFileImage
import com.belajar.storyapp.util.rotateBitmap
import com.belajar.storyapp.util.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UploadStoryActivity : AppCompatActivity() {
    private var _binding: ActivityUploadStoryBinding? = null
    private val binding get() = _binding
    private var locationProvider: FusedLocationProviderClient? = null
    private val viewModel: UploadStoryViewModel by viewModels()
    private var token: String? = null
    private var myLocation: Location? = null
    private var image: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        locationProvider = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQ_CODE_PERMIT
            )
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        token = intent.getStringExtra(EXTRA_TOKEN_UPLOAD).toString()

        binding?.apply {
            btnCamera.setOnClickListener { startCameraX() }
            btnGallery.setOnClickListener { startGallery() }
            btnStoryUpload.setOnClickListener { uploadStory() }
            addLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    getMyLocation()
                } else {
                    myLocation = null
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CODE_PERMIT) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    R.string.not_permitted,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            image = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(image?.path),
                isBackCamera
            )

            binding?.tvStoryImage?.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@UploadStoryActivity)

            image = myFile

            binding?.tvStoryImage?.setImageURI(selectedImg)
        }
    }

    private fun uploadStory() {
        if (image != null){
            val file = reduceFileImage(image as File)
            binding?.apply {
                val description = edtStoryDesc.text
                if (description.isBlank()) {
                    edtStoryDesc.requestFocus()
                    edtStoryDesc.error = getString(R.string.empty_desc)
                }else{
                    val descMediaTyped = description.toString().toRequestBody("text/plain".toMediaType())
                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imageMultipart = MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        requestImageFile
                    )
                    var lat: RequestBody? = null
                    var lon: RequestBody? = null

                    if (myLocation != null) {
                        lat = myLocation?.latitude.toString().toRequestBody("text/plain".toMediaType())
                        lon = myLocation?.longitude.toString().toRequestBody("text/plain".toMediaType())
                        Log.e(TAG, "Lon: $lon, Lat: $lat")

                    }

                    viewModel.postStories(token!!, imageMultipart, descMediaTyped, lat, lon)
                    viewModel.isUploaded.observe(this@UploadStoryActivity){
                        if (it == true) {
                            val message = viewModel.responseMessage.toString()
                            Toast.makeText(this@UploadStoryActivity, message, Toast.LENGTH_SHORT)
                            Intent(
                                this@UploadStoryActivity,
                                MainActivity::class.java
                            ).also { intent ->
                                intent.putExtra(EXTRA_TOKEN_MAIN, token)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                    viewModel.isError.observe(this@UploadStoryActivity){
                        if (it == true){
                            val message = viewModel.responseMessage.toString()
                            Toast.makeText(this@UploadStoryActivity, message, Toast.LENGTH_SHORT)
                        }
                    }
                }
            }
        }else{
            Toast.makeText(this@UploadStoryActivity, getString(R.string.choose_a_pict), Toast.LENGTH_SHORT).show()
        }
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
            locationProvider?.lastLocation?.addOnSuccessListener { location ->
                if (location != null) {
                    myLocation = location
                    Log.e(TAG, "Lon: ${location.longitude}, Lat: ${location.latitude}, Acc: ${location.accuracy}")
                } else {
                    Toast.makeText(this, getString(R.string.not_permitted), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            if (isLoading) pbUpload.visibility = View.VISIBLE else pbUpload.visibility = View.INVISIBLE
            btnCamera.isClickable = !isLoading
            btnCamera.isEnabled = !isLoading
            btnStoryUpload.isClickable = !isLoading
            btnStoryUpload.isEnabled = !isLoading
            btnGallery.isClickable = !isLoading
            btnGallery.isEnabled = !isLoading
        }
    }

    companion object {
        const val TAG = "UploadActivity"
        const val EXTRA_TOKEN_UPLOAD = "token in upload activity"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}