package com.example.tournamaker.data.repository

import com.example.tournamaker.data.model.Team
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class TeamRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val teamsCollection = firestore.collection("teams")

    suspend fun createTeam(team: Team): Result<Team> {
        return try {
            val docRef = teamsCollection.add(team).await()
            Result.success(team.copy(id = docRef.id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTeamsByCreator(creatorId: String): List<Team> {
        return try {
            teamsCollection.whereEqualTo("creatorId", creatorId).get().await().documents.mapNotNull {
                it.toObject<Team>()?.copy(id = it.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTeamsByParticipant(username: String): List<Team> {
        return try {
            teamsCollection.whereArrayContains("participants", username).get().await().documents.mapNotNull {
                it.toObject<Team>()?.copy(id = it.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTeamById(teamId: String): Team? {
        return try {
            teamsCollection.document(teamId).get().await()
                .toObject<Team>()?.copy(id = teamId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAllTeams(limit: Int? = null): List<Team> {
        return try {
            var query: Query = teamsCollection

            if (limit != null) {
                query = query.limit(limit.toLong())
            }

            query.get().await().documents.mapNotNull {
                it.toObject<Team>()?.copy(id = it.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun requestToJoinTeam(teamId: String, userId: String): Result<Unit> {
        return try {
            teamsCollection.document(teamId).update("requestedUsers", FieldValue.arrayUnion(userId)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}