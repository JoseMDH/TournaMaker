package com.example.tournamaker.data.repository

import com.example.tournamaker.data.model.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NotificationRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val notificationsCollection = firestore.collection("notifications")

    suspend fun createNotification(notification: Notification) {
        try {
            notificationsCollection.add(notification).await()
        } catch (e: Exception) {
            // Handle exceptions, e.g., logging
        }
    }

    fun getNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = notificationsCollection
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val notifications = snapshot?.toObjects<Notification>() ?: emptyList()
                trySend(notifications)
            }
        awaitClose { listener.remove() }
    }

    fun getUnreadNotificationCount(userId: String): Flow<Int> = callbackFlow {
        val listener = notificationsCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("read", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.size() ?: 0)
            }
        awaitClose { listener.remove() }
    }

    suspend fun markAllNotificationsAsRead(userId: String) {
        try {
            val unreadNotifications = notificationsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("read", false)
                .get()
                .await()

            val batch = firestore.batch()
            for (document in unreadNotifications.documents) {
                batch.update(document.reference, "read", true)
            }
            batch.commit().await()
        } catch (e: Exception) {
            // Handle exceptions, e.g., logging
        }
    }
}