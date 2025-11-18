package com.example.tournamaker.data.repository

import com.example.tournamaker.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    suspend fun getAll(): List<User> {
        return try {
            usersCollection.get().await().documents.mapNotNull {
                it.toObject<User>()?.copy(id = it.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getById(id: String): User? {
        return try {
            usersCollection.document(id).get().await().toObject<User>()?.copy(id = id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getByEmail(email: String): User? {
        return try {
            usersCollection
                .whereEqualTo("email", email)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.let { document ->
                    document.toObject<User>()?.copy(id = document.id)
                }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun create(user: User): Result<User> {
        return try {
            val docRef = usersCollection.add(user).await()
            Result.success(user.copy(id = docRef.id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun update(id: String, data: Map<String, Any>): Result<Unit> {
        return try {
            usersCollection.document(id).update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun delete(id: String): Result<Unit> {
        return try {
            usersCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeById(id: String): Flow<User?> = callbackFlow {
        val listener = usersCollection.document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject<User>()?.copy(id = snapshot.id))
            }
        awaitClose { listener.remove() }
    }
}
