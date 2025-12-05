package com.example.tournamaker.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Notification(
    val id: String = "",
    val userId: String = "",
    val message: String = "",
    @ServerTimestamp
    val timestamp: Date? = null,
    val read: Boolean = false
)