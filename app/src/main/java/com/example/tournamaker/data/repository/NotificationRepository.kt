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

    suspend fun countUnreadNotifications(userId: String): Int {
        return try {
            val query = notificationsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()
            query.size()
        } catch (e: Exception) {
            0
        }
    }

    suspend fun markAllAsRead(userId: String) {
        try {
            val unreadNotifications = notificationsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()

            val batch = firestore.batch()
            for (document in unreadNotifications.documents) {
                batch.update(document.reference, "isRead", true)
            }
            batch.commit().await()
        } catch (e: Exception) {
            // Handle error
        }
    }
}