package com.example.tournamaker.data.repository

import com.example.tournamaker.data.model.Match
import com.example.tournamaker.data.model.Team
import com.example.tournamaker.data.model.Tournament
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class TournamentRepository(
    private val teamRepository: TeamRepository = TeamRepository()
) {

    private val firestore = FirebaseFirestore.getInstance()
    private val tournamentsCollection = firestore.collection("tournaments")
    private val matchesCollection = firestore.collection("matches")

    suspend fun getAllTournaments(limit: Int? = null): List<Tournament> {
        return try {
            val query = if (limit != null) {
                tournamentsCollection.limit(limit.toLong())
            } else {
                tournamentsCollection
            }
            query.get().await().documents.mapNotNull {
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
            val tournamentRef = tournamentsCollection.document()
            val round1Matches = mutableListOf<String>()

            val batch = firestore.batch()

            for (i in 0 until tournament.teamsNum / 2) {
                val matchRef = matchesCollection.document()
                val match = Match(
                    id = matchRef.id,
                    name = "${tournament.name} - Round 1",
                    tournamentId = tournamentRef.id
                )
                batch.set(matchRef, match)
                round1Matches.add(matchRef.id)
            }

            val newTournament = tournament.copy(
                id = tournamentRef.id,
                rounds = mapOf("round1" to round1Matches)
            )
            batch.set(tournamentRef, newTournament)

            batch.commit().await()
            Result.success(newTournament)
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

    suspend fun requestToJoinTournament(tournamentId: String, teamId: String): Result<Unit> {
        return try {
            val tournament = getTournamentById(tournamentId)
            val team = teamRepository.getTeamById(teamId)
            if (tournament != null && team != null) {
                if (tournament.teams.size < tournament.teamsNum) {
                    tournamentsCollection.document(tournamentId).update("teams", FieldValue.arrayUnion(teamId)).await()
                    addTeamToBracket(tournament, team)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Tournament is full"))
                }
            } else {
                Result.failure(Exception("Tournament or team not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun addTeamToBracket(tournament: Tournament, team: Team) {
        val round1 = tournament.rounds["round1"] ?: return
        for (matchId in round1) {
            val match = getMatchById(matchId) ?: continue
            if (match.team1Id == null) {
                matchesCollection.document(matchId).update(
                    "team1Id", team.id,
                    "team1Name", team.name,
                    "team1Image", team.image
                ).await()
                return
            } else if (match.team2Id == null) {
                matchesCollection.document(matchId).update(
                    "team2Id", team.id,
                    "team2Name", team.name,
                    "team2Image", team.image
                ).await()
                return
            }
        }
    }

    private suspend fun getMatchById(matchId: String): Match? {
        return try {
            matchesCollection.document(matchId).get().await().toObject<Match>()?.copy(id = matchId)
        } catch (e: Exception) {
            null
        }
    }
}