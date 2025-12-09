package com.example.tournamaker.data.repository

import com.example.tournamaker.data.model.Match
import com.example.tournamaker.data.model.Team
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class MatchRepository(
    private val teamRepository: TeamRepository = TeamRepository()
) {

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

    suspend fun getMatchesByIds(matchIds: List<String>): List<Match> {
        if (matchIds.isEmpty()) {
            return emptyList()
        }
        return try {
            matchesCollection.whereIn(FieldPath.documentId(), matchIds).get().await().documents.mapNotNull {
                it.toObject<Match>()?.copy(id = it.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun joinMatch(matchId: String, teamId: String): Result<Unit> {
        return try {
            val match = getMatchById(matchId)
            val team = teamRepository.getTeamById(teamId)
            if (match != null && team != null) {
                if (match.team1Id == null) {
                    matchesCollection.document(matchId).update(
                        "team1Id", team.id,
                        "team1Name", team.name,
                        "team1Image", team.image
                    ).await()
                    Result.success(Unit)
                } else if (match.team2Id == null) {
                    matchesCollection.document(matchId).update(
                        "team2Id", team.id,
                        "team2Name", team.name,
                        "team2Image", team.image
                    ).await()
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Match is full"))
                }
            } else {
                Result.failure(Exception("Match or team not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMatchStatus(matchId: String, status: String): Result<Unit> {
        return try {
            matchesCollection.document(matchId).update("status", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMatchScore(matchId: String, score1: Int, score2: Int): Result<Unit> {
        return try {
            matchesCollection.document(matchId).update(
                "team1Score", score1,
                "team2Score", score2
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}