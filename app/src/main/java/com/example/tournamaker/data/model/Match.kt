package com.example.tournamaker.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Match(
    val id: String = "",
    val tournamentId: String = "",
    val team1Id: String = "",
    val team2Id: String = "",
    val team1Name: String = "",
    val team2Name: String = "",
    val team1Score: Int = 0,
    val team2Score: Int = 0,
    val date: String = "",
    val round: Int = 0,
    val status: String = "PENDING", // PENDING, IN_PROGRESS, FINISHED
    val imageUrl: String = "",
    @ServerTimestamp
    val creationDate: Date? = null
) {
    constructor() : this(
        id = "",
        tournamentId = "",
        team1Id = "",
        team2Id = "",
        team1Name = "",
        team2Name = "",
        team1Score = 0,
        team2Score = 0,
        date = "",
        round = 0,
        status = "PENDING",
        imageUrl = "",
        creationDate = null
    )
}