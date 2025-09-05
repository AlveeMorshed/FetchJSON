package com.alvee.fetchjson.utils

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class DataStoreManager(private val dataStore: DataStore<Preferences>) {

    private val gson = Gson()
    private val keyAlias = "FetchJSONPasswordKey"

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "AI PharmaDataStore")

        @Volatile
        private var INSTANCE: DataStoreManager? = null
        val CURRENT_USER_ID = stringPreferencesKey(Constants.CURRENT_USER_ID)
        val USER_EMAIL = stringPreferencesKey(Constants.USER_EMAIL)
        val USER_PASSWORD = stringPreferencesKey(Constants.USER_PASSWORD)
        val PASSWORD_IV = stringPreferencesKey(Constants.PASSWORD_IV)
        val IS_LOGGED_IN = booleanPreferencesKey(Constants.IS_LOGGED_IN)
        val SHOULD_LOG_OUT = booleanPreferencesKey(Constants.SHOULD_LOG_OUT)

        fun getInstance(context: Context): DataStoreManager {
            return INSTANCE ?: synchronized(this) {
                val instance = DataStoreManager(context.dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
    init {
        generateOrGetSecretKey()
    }
    private fun generateUserId(email: String): String {
        // Create a consistent hash-based ID from email
        return email.hashCode().toString().removePrefix("-")
    }

    // Save data
    fun put(key: Preferences.Key<String>, value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    fun clear(key: Preferences.Key<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences.remove(key)
            }
        }
    }

    fun put(key: Preferences.Key<Boolean>, value: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    fun put(key: Preferences.Key<Int>, value: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    fun put(key: Preferences.Key<Long>, value: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }
    // Retrieve data
    fun getString(key: Preferences.Key<String>): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[key]
        }
    }

    fun getBoolean(key: Preferences.Key<Boolean>): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: false
        }
    }

    fun getInt(key: Preferences.Key<Int>): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: 0
        }
    }

    fun getLong(key: Preferences.Key<Long>): Flow<Long> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: 0L
        }
    }

    // Clear data
    fun clear() {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }

    // Save custom object
    fun <T> putObject(key: Preferences.Key<String>, value: T) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                val jsonString = gson.toJson(value)
                preferences[key] = jsonString
            }
        }
    }

    // Retrieve custom object
    fun <T> getObject(key: Preferences.Key<String>, clazz: Class<T>): Flow<T?> {
        return dataStore.data.map { preferences ->
            val jsonString = preferences[key]
            jsonString?.let { gson.fromJson(it, clazz) }
        }
    }

    private fun generateOrGetSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        return if (keyStore.containsAlias(keyAlias)) {
            keyStore.getKey(keyAlias, null) as SecretKey
        } else {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }

    private fun encryptPassword(password: String): Pair<String, String> {
        val secretKey = generateOrGetSecretKey()
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val iv = cipher.iv
        val encryptedPassword = cipher.doFinal(password.toByteArray())

        return Pair(
            Base64.encodeToString(encryptedPassword, Base64.DEFAULT),
            Base64.encodeToString(iv, Base64.DEFAULT)
        )
    }

    private fun decryptPassword(encryptedPassword: String, ivString: String): String {
        val secretKey = generateOrGetSecretKey()
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")

        val iv = Base64.decode(ivString, Base64.DEFAULT)
        val ivParameterSpec = IvParameterSpec(iv)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)

        val encryptedBytes = Base64.decode(encryptedPassword, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes)
    }

    suspend fun registerUser(email: String, password: String): Boolean {
        return try {
            val (encryptedPassword, iv) = encryptPassword(password)
            val userId = generateUserId(email)

            dataStore.edit { preferences ->
                preferences[USER_EMAIL] = email
                preferences[USER_PASSWORD] = encryptedPassword
                preferences[PASSWORD_IV] = iv
                preferences[CURRENT_USER_ID] = userId
                preferences[IS_LOGGED_IN] = true
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            val storedEmail = getString(USER_EMAIL).first()
            val encryptedPassword = getString(USER_PASSWORD).first()
            val iv = getString(PASSWORD_IV).first()

            if (storedEmail == email && encryptedPassword != null && iv != null) {
                val decryptedPassword = decryptPassword(encryptedPassword, iv)
                if (decryptedPassword == password) {
                    val userId = generateUserId(email)
                    dataStore.edit { preferences ->
                        preferences[CURRENT_USER_ID] = userId
                        preferences[IS_LOGGED_IN] = true
                    }
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getCurrentUserId(): String? {
        return getString(CURRENT_USER_ID).first()
    }

    fun getCurrentUserIdFlow(): Flow<String?> {
        return getString(CURRENT_USER_ID)
    }

    suspend fun logoutUser() {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences[CURRENT_USER_ID] = ""
        }
    }

    fun isUserRegistered(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[USER_EMAIL] != null &&
                    preferences[USER_PASSWORD] != null &&
                    preferences[PASSWORD_IV] != null
        }
    }

    fun isUserLoggedIn(): Flow<Boolean> {
        return getBoolean(IS_LOGGED_IN)
    }
}