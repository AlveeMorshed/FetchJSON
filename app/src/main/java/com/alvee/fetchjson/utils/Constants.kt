package com.alvee.fetchjson.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {
    const val BASE_URL = "https://jsonplaceholder.typicode.com"
    const val SHOULD_LOG_OUT = "should_log_out"
    const val CURRENT_USER_ID = "current_user_id"
    const val USER_EMAIL = "user_email"
    const val USER_PASSWORD = "user_password"
    const val PASSWORD_IV = "password_iv"
    const val IS_LOGGED_IN = "is_logged_in"
}