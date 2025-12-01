package com.example.tournamaker.data.repository

import com.example.tournamaker.data.model.Match
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class MatchRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val matchesCollection = firestore.collection("matches")

    suspend fun getAllMatches(limit: Int? = null): List<Match> {
        return try {
            var query: Query = matchesCollection

            if (limit != null) {
                query = query.limit(limit.toLong())
            }

            query.get().await().documents.mapNotNull {
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

    suspend fun getMatchesByCreator(creatorId: String): List<Match> {
        return try {
            matchesCollection.whereEqualTo("creatorId", creatorId).get().await().documents.mapNotNull {
                it.toObject<Match>()?.copy(id = it.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMatchesByTournamentIds(tournamentIds: List<String>): List<Match> {
        if (tournamentIds.isEmpty()) {
            return emptyList()
        }
        return try {
            matchesCollection.whereIn("tournamentId", tournamentIds).get().await().documents.mapNotNull {
                it.toObject<Match>()?.copy(id = it.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}