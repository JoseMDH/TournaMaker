package com.example.tournamaker.data.repository

import com.example.tournamaker.data.model.JoinRequest
import com.example.tournamaker.data.model.RequestStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class JoinRequestRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val requestsCollection = firestore.collection("join_requests")

    /**
     * Creates a new join request document in Firestore.
     */
    suspend fun createJoinRequest(request: JoinRequest): Result<Unit> {
        return try {
            requestsCollection.add(request).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches all pending join requests for a specific user (the owner).
     */
    suspend fun getPendingRequestsForUser(userId: String): List<JoinRequest> {
        return try {
            requestsCollection
                .whereEqualTo("ownerId", userId)
                .whereEqualTo("status", RequestStatus.PENDING)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents.mapNotNull { doc ->
                    doc.toObject<JoinRequest>()?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Updates the status of a specific join request.
     */
    suspend fun updateRequestStatus(requestId: String, status: RequestStatus): Result<Unit> {
        return try {
            requestsCollection.document(requestId).update("status", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}