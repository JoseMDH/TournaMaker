package com.example.tournamaker.data.repository

import com.example.tournamaker.data.model.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class NotificationRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val notificationsCollection = firestore.collection("notifications")

    suspend fun createNotification(notification: Notification) {
        try {
            notificationsCollection.add(notification).await()
        } catch (e: Exception) {
            // Handle exception
        }
    }

    suspend fun getNotificationsForUser(userId: String): List<Notification> {
        return try {
            notificationsCollection.whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Notification::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}