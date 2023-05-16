package com.example.diplom.util

import android.content.Context
import android.content.SharedPreferences

class TokenService(context: Context) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    fun saveToken(token: String){
        put(PREF_TOKEN, token)
    }

    fun getToken(): String {
        return get(PREF_TOKEN, String::class.java)
    }

    fun containsToken(): Boolean{
        return sharedPref.contains(PREF_TOKEN)
    }

    fun clearToken() {
        sharedPref.edit().run {
            remove(PREF_TOKEN)
        }.apply()
    }

    fun saveFCMToken(token: String){
        put(FCM_TOKEN, token)
    }

    fun getFCMToken(): String {
        return get(FCM_TOKEN, String::class.java)
    }

    fun containsFCMToken(): Boolean{
        return sharedPref.contains(FCM_TOKEN)
    }

    fun clearFCMToken() {
        sharedPref.edit().run {
            remove(FCM_TOKEN)
        }.apply()
    }

    fun saveUserId(id: Long){
        put(USER_ID, id)
    }

    fun getUserId(): Long {
        return get(USER_ID, Long::class.java)
    }

    fun containsUserId(): Boolean{
        return sharedPref.contains(USER_ID)
    }

    fun clearUserId() {
        sharedPref.edit().run {
            remove(USER_ID)
        }.apply()
    }

    private fun <T> get(key: String, clazz: Class<T>): T =
        when (clazz) {
            String::class.java -> sharedPref.getString(key, "")
            Boolean::class.java -> sharedPref.getBoolean(key, false)
            Float::class.java -> sharedPref.getFloat(key, -1f)
            Double::class.java -> sharedPref.getFloat(key, -1f)
            Int::class.java -> sharedPref.getInt(key, -1)
            Long::class.java -> sharedPref.getLong(key, -1L)
            else -> null
        } as T

    private fun <T> put(key: String, data: T) {
        val editor = sharedPref.edit()
        when (data) {
            is String -> editor.putString(key, data)
            is Boolean -> editor.putBoolean(key, data)
            is Float -> editor.putFloat(key, data)
            is Double -> editor.putFloat(key, data.toFloat())
            is Int -> editor.putInt(key, data)
            is Long -> editor.putLong(key, data)
        }
        editor.apply()
    }
}