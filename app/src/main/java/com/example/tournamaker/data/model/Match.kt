package com.example.tournamaker.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Match(
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val tournamentId: String = "",
    val creatorId: String = "",

    val team1Id: String? = null,
    val team2Id: String? = null,
    val team1Name: String? = null,
    val team2Name: String? = null,
    val team1Image: String? = null,
    val team2Image: String? = null,

    val team1Score: Int = 0,
    val team2Score: Int = 0,
    val date: String = "",
    val hour: String = "",
    val status: String = "PENDING", // PENDING, IN_PROGRESS, FINISHED
    val winnerId: String? = null,

    @ServerTimestamp
    val creationDate: Date? = null
) {
    constructor() : this(
        id = "",
        name = "",
        image = "",
        tournamentId = "",
        creatorId = "",
        team1Id = null,
        team2Id = null,
        team1Name = null,
        team2Name = null,
        team1Image = null,
        team2Image = null,
        team1Score = 0,
        team2Score = 0,
        date = "",
        hour = "",
        status = "PENDING",
        winnerId = null,
        creationDate = null
    )
}
