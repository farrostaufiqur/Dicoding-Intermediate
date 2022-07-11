package com.belajar.storyapp.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences private constructor(private val dataStore: DataStore<Preferences>) {


    fun getIsDarkMode(): Flow<Boolean?> {
        return dataStore.data.map {
            it[IS_DARK_MODE]
        }
    }

    fun getUserToken(): Flow<String?> {
        return dataStore.data.map {
            it[USER_TOKEN]
        }
    }

    suspend fun saveIsDarkMode(isDark: Boolean) {
        dataStore.edit {
            it[IS_DARK_MODE] = isDark
        }
    }

    suspend fun saveUserToken(token: String) {
        dataStore.edit {
            it[USER_TOKEN] = token
        }
    }

    suspend fun clearUserData() {
        dataStore.edit {
            it.clear()
        }
    }

    companion object {
        private val IS_DARK_MODE = booleanPreferencesKey("app.is.dark.mode")
        private val USER_TOKEN = stringPreferencesKey("app.user.token")

        @Volatile
        private var INSTANCE: AppPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): AppPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AppPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}