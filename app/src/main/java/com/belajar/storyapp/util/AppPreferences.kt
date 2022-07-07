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

    fun getUserId(): Flow<String?>{
        return dataStore.data.map {
            it[USER_ID]
        }
    }

    fun getUserName(): Flow<String?>{
        return dataStore.data.map {
            it[USER_NAME]
        }
    }

    fun getUserEmail(): Flow<String?>{
        return dataStore.data.map {
            it[USER_EMAIL]
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

    suspend fun saveUserId(id: String) {
        dataStore.edit {
            it[USER_ID] = id
        }
    }

    suspend fun saveUserName(name: String) {
        dataStore.edit {
            it[USER_NAME] = name
        }
    }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit {
            it[USER_EMAIL] = email
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
        private val USER_ID = stringPreferencesKey("app.user.id")
        private val USER_NAME = stringPreferencesKey("app.user.name")
        private val USER_EMAIL = stringPreferencesKey("app.email.email")

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