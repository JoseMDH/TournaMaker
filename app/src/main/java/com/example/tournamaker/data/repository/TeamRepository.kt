package com.example.tournamaker.data.repository

import com.example.tournamaker.data.model.Team
import com.google.firebase.firestore.FirebaseFirestore
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
}