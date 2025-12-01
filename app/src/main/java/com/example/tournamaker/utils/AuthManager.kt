package com.example.tournamaker.utils

import android.content.Context
import com.example.tournamaker.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson

class AuthManager private constructor(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("AuthManager", Context.MODE_PRIVATE)
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun saveUser(user: User) {
        val userJson = Gson().toJson(user)
        sharedPreferences.edit().putString("user", userJson).apply()
    }

    fun getUser(): User? {
        val userJson = sharedPreferences.getString("user", null)
        return if (userJson != null) {
            Gson().fromJson(userJson, User::class.java)
        } else {
            null
        }
    }

    fun isLoggedIn(): Boolean {
        return getUser() != null
    }

    fun logout() {
        firebaseAuth.signOut()
        sharedPreferences.edit().remove("user").apply()
    }

    fun sendPasswordResetEmail(email: String, onComplete: () -> Unit) {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
            onComplete()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthManager? = null

        fun getInstance(context: Context): AuthManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthManager(context).also { INSTANCE = it }
            }
        }
    }
}