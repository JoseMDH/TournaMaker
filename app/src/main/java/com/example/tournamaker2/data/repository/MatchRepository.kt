package com.example.tournamaker2.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.example.tournamaker2.data.model.Match
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MatchRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val matchesCollection = firestore.collection("matches")

    suspend fun getAll(): List<Match> {
        return try {
            matchesCollection.get().await().documents.mapNotNull {
                it.toObject<Match>()?.copy(id = it.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getById(id: String): Match? {
        return try {
            matchesCollection.document(id).get().await()
                .toObject<Match>()?.copy(id = id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun create(match: Match): Result<Match> {
        return try {
            val docRef = matchesCollection.add(match).await()
            Result.success(match.copy(id = docRef.id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun update(id: String, data: Map<String, Any>): Result<Unit> {
        return try {
            matchesCollection.document(id).update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun delete(id: String): Result<Unit> {
        return try {
            matchesCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeAll(): Flow<List<Match>> = callbackFlow {
        val listener = matchesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val matches = snapshot?.documents?.mapNotNull {
                it.toObject<Match>()?.copy(id = it.id)
            } ?: emptyList()
            trySend(matches)
        }
        awaitClose { listener.remove() }
    }
}
