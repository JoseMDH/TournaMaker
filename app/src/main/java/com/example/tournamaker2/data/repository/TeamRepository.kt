package com.example.tournamaker2.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.example.tournamaker2.data.model.Team
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TeamRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val teamsCollection = firestore.collection("teams")

    suspend fun getAll(): List<Team> {
        return try {
            teamsCollection.get().await().documents.mapNotNull {
                it.toObject<Team>()?.copy(id = it.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getById(id: String): Team? {
        return try {
            teamsCollection.document(id).get().await().toObject<Team>()?.copy(id = id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getTeamsByUser(userName: String): List<Team> {
        return try {
            teamsCollection
                .whereArrayContains("participants", userName)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject<Team>()?.copy(id = it.id) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTeamsByCreator(userId: String): List<Team> {
        return try {
            teamsCollection
                .whereEqualTo("creator", userId)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject<Team>()?.copy(id = it.id) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun create(team: Team): Result<Team> {
        return try {
            val docRef = teamsCollection.add(team).await()
            Result.success(team.copy(id = docRef.id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun update(id: String, data: Map<String, Any>): Result<Unit> {
        return try {
            teamsCollection.document(id).update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun delete(id: String): Result<Unit> {
        return try {
            teamsCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeAll(): Flow<List<Team>> = callbackFlow {
        val listener = teamsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val teams = snapshot?.documents?.mapNotNull {
                it.toObject<Team>()?.copy(id = it.id)
            } ?: emptyList()
            trySend(teams)
        }
        awaitClose { listener.remove() }
    }
}
