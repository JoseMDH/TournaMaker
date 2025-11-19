package com.example.tournamaker.data.repository

import com.example.tournamaker.data.model.Tournament
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class TournamentRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val tournamentsCollection = firestore.collection("tournaments")

    suspend fun getAllTournaments(): List<Tournament> {
        return try {
            tournamentsCollection.get().await().documents.mapNotNull {
                it.toObject<Tournament>()?.copy(id = it.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTournamentById(tournamentId: String): Tournament? {
        return try {
            tournamentsCollection.document(tournamentId).get().await()
                .toObject<Tournament>()?.copy(id = tournamentId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createTournament(tournament: Tournament): Result<Tournament> {
        return try {
            val docRef = tournamentsCollection.add(tournament).await()
            Result.success(tournament.copy(id = docRef.id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTournamentsByCreator(userId: String): List<Tournament> {
        return try {
            tournamentsCollection.whereEqualTo("creatorId", userId).get().await().documents.mapNotNull {
                it.toObject<Tournament>()?.copy(id = it.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}