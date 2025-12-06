package com.example.tournamaker.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Match(
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val tournamentId: String = "",
    val creatorId: String = "",
    val team1Name: String = "",
    val team2Name: String = "",
    val team1Score: Int = 0,
    val team2Score: Int = 0,
    val date: String = "",
    val hour: String = "",
    val status: String = "PENDING", // PENDING, IN_PROGRESS, FINISHED
    val requestedTeams: List<String> = emptyList(),

    @ServerTimestamp
    val creationDate: Date? = null
) {
    constructor() : this(
        id = "",
        name = "",
        image = "",
        tournamentId = "",
        creatorId = "",
        team1Name = "",
        team2Name = "",
        team1Score = 0,
        team2Score = 0,
        date = "",
        hour = "",
        status = "PENDING",
        requestedTeams = emptyList(),
        creationDate = null
    )
}