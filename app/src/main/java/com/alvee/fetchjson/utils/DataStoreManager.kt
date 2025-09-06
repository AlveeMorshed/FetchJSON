package com.alvee.fetchjson.utils

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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

private const val TAG = "DataStoreManager"

class DataStoreManager(private val dataStore: DataStore<Preferences>) {

    data class UserData(
        val email: String,
        val encryptedPassword: String,
        val passwordIv: String,
        val userId: Int
    )

    data class MultiUserState(
        val users: Map<String, UserData> = emptyMap(),
        val currentUserId: Int? = null,
        val isLoggedIn: Boolean = false
    )

    private val gson = Gson()
    private val keyAlias = "FetchJSONPasswordKey"

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "FetchJSON DataStore")

        @Volatile
        private var INSTANCE: DataStoreManager? = null
        val MULTI_USER_DATA = stringPreferencesKey(Constants.MULTI_USER_DATA)
        val CURRENT_USER_ID = intPreferencesKey(Constants.CURRENT_USER_ID)
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

    private fun generateUserId(email: String): Int {
        return kotlin.math.abs(email.hashCode())
    }

    private suspend fun saveUserState(state: MultiUserState) {
        dataStore.edit { preferences ->
            preferences[MULTI_USER_DATA] = gson.toJson(state)
            preferences[CURRENT_USER_ID] = state.currentUserId ?: 0
            preferences[IS_LOGGED_IN] = state.isLoggedIn
        }
    }

    private suspend fun loadUserState(): MultiUserState {
        val jsonString = getString(MULTI_USER_DATA).first()
        return if (jsonString != null) {
            gson.fromJson(jsonString, MultiUserState::class.java)
        } else {
            MultiUserState()
        }
    }

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

    fun clear() {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }

    fun <T> putObject(key: Preferences.Key<String>, value: T) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                val jsonString = gson.toJson(value)
                preferences[key] = jsonString
            }
        }
    }

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
            val keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
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
            val currentState = loadUserState()
            if (currentState.users.containsKey(email)) {
                return false
            }
            val (encryptedPassword, iv) = encryptPassword(password)
            val userId = generateUserId(email)

            val userData = UserData(email, encryptedPassword, iv, userId)
            val newState = currentState.copy(
                users = currentState.users + (email to userData),
                currentUserId = userId,
                isLoggedIn = true
            )

            saveUserState(newState)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed", e)
            false
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            val currentState = loadUserState()
            val userData = currentState.users[email] ?: return false

            val decryptedPassword = decryptPassword(userData.encryptedPassword, userData.passwordIv)

            if (decryptedPassword == password) {
                val newState = currentState.copy(
                    currentUserId = userData.userId,
                    isLoggedIn = true
                )
                saveUserState(newState)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login failed", e)
            false
        }
    }

    suspend fun getCurrentUserId(): Int? {
        return getInt(CURRENT_USER_ID).first().takeIf { it != 0 }
    }

    fun getCurrentUserIdFlow(): Flow<Int> {
        return getInt(CURRENT_USER_ID)
    }

    suspend fun isEmailAlreadyRegistered(email: String): Boolean {
        return try {
            val currentState = loadUserState()
            currentState.users.containsKey(email)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check email registration", e)
            false
        }
    }

    suspend fun getAllRegisteredUsers(): List<String> {
        return try {
            val currentState = loadUserState()
            currentState.users.keys.toList()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get registered users", e)
            emptyList()
        }
    }

    suspend fun getCurrentUserEmail(): String? {
        return try {
            val currentState = loadUserState()
            val currentUserId = currentState.currentUserId ?: return null
            currentState.users.values.find { it.userId == currentUserId }?.email
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get current user email", e)
            null
        }
    }

    suspend fun removeUser(email: String): Boolean {
        return try {
            val currentState = loadUserState()
            val userData = currentState.users[email] ?: return false

            val newUsers = currentState.users.toMutableMap()
            newUsers.remove(email)

            val newState = if (currentState.currentUserId == userData.userId) {
                currentState.copy(
                    users = newUsers,
                    currentUserId = null,
                    isLoggedIn = false
                )
            } else {
                currentState.copy(users = newUsers)
            }

            saveUserState(newState)
            Log.d(TAG, "User removed: $email")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove user", e)
            false
        }
    }

    suspend fun logoutUser() {
        try {
            val currentState = loadUserState()
            val newState = currentState.copy(
                currentUserId = null,
                isLoggedIn = false
            )
            saveUserState(newState)
            Log.d(TAG, "User logged out")
        } catch (e: Exception) {
            Log.e(TAG, "Logout failed", e)
        }
    }

    fun isUserLoggedIn(): Flow<Boolean> {
        return getBoolean(IS_LOGGED_IN)
    }
}