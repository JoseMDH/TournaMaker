package com.example.tournamaker.data.repository


import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.example.tournamaker.data.model.Tournament
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TournamentRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val tournamentsCollection = firestore.collection("tournaments")

    suspend fun getAll(): List<Tournament> {
        return try {
            tournamentsCollection.get().await().documents.mapNotNull {
                it.toObject<Tournament>()?.copy(id = it.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getById(id: String): Tournament? {
        return try {
            tournamentsCollection.document(id).get().await()
                .toObject<Tournament>()?.copy(id = id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun create(tournament: Tournament): Result<Tournament> {
        return try {
            val docRef = tournamentsCollection.add(tournament).await()
            Result.success(tournament.copy(id = docRef.id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun update(id: String, data: Map<String, Any>): Result<Unit> {
        return try {
            tournamentsCollection.document(id).update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun delete(id: String): Result<Unit> {
        return try {
            tournamentsCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeAll(): Flow<List<Tournament>> = callbackFlow {
        val listener = tournamentsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val tournaments = snapshot?.documents?.mapNotNull {
                it.toObject<Tournament>()?.copy(id = it.id)
            } ?: emptyList()
            trySend(tournaments)
        }
        awaitClose { listener.remove() }
    }
}
