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
}