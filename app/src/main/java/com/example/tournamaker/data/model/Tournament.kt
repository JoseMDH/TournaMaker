package com.example.tournamaker.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Tournament(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val game: String = "",
    val date: String = "",
    val place: String = "",
    val maxParticipants: Int = 0,
    val creatorId: String = "",
    val imageUrl: String = "",
    @ServerTimestamp
    val creationDate: Date? = null,
    val participants: List<String> = emptyList(),
    val rounds: Int = 0
) {
    constructor() : this(
        id = "",
        name = "",
        description = "",
        game = "",
        date = "",
        place = "",
        maxParticipants = 0,
        creatorId = "",
        imageUrl = "",
        creationDate = null,
        participants = emptyList(),
        rounds = 0
    )
}