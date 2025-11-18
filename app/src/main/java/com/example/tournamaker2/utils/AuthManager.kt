package com.example.tournamaker2.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.example.tournamaker2.data.model.User

class AuthManager private constructor(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("tournamaker_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_CURRENT_USER = "current_user"

        @Volatile
        private var INSTANCE: AuthManager? = null

        fun getInstance(context: Context): AuthManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    fun setUser(user: User) {
        val userJson = gson.toJson(user)
        prefs.edit().putString(KEY_CURRENT_USER, userJson).apply()
    }

    fun getUser(): User? {
        val userJson = prefs.getString(KEY_CURRENT_USER, null) ?: return null
        return try {
            gson.fromJson(userJson, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun isLoggedIn(): Boolean = getUser() != null

    fun logout() {
        prefs.edit().remove(KEY_CURRENT_USER).apply()
    }
}
