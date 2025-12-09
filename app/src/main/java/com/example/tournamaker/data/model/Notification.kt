package com.example.tournamaker.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Notification(
    val id: String = "",
    val userId: String = "", // ID del usuario que recibe la notificación
    val message: String = "",
    @ServerTimestamp
    val timestamp: Date? = null,
    val isRead: Boolean = false
)
