package com.example.tournamaker.data.repository

import com.example.tournamaker.data.model.Match
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class MatchRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val matchesCollection = firestore.collection("matches")

    suspend fun getAllMatches(): List<Match> {
        return try {
            matchesCollection.get().await().documents.mapNotNull {
                it.toObject<Match>()?.copy(id = it.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMatchById(matchId: String): Match? {
        return try {
            matchesCollection.document(matchId).get().await()
                .toObject<Match>()?.copy(id = matchId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createMatch(match: Match): Result<Match> {
        return try {
            val docRef = matchesCollection.add(match).await()
            Result.success(match.copy(id = docRef.id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}