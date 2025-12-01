package com.example.tournamaker.data.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Tournament(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val date: String = "",
    val place: String = "",
    val image: String = "",
    val organizer: String = "",
    val creatorId: String = "",

    @get:PropertyName("prize_pool") @set:PropertyName("prize_pool")
    var prizePool: String = "",

    @get:PropertyName("entry_tax") @set:PropertyName("entry_tax")
    var entryTax: String = "",

    @get:PropertyName("teams_num") @set:PropertyName("teams_num")
    var teamsNum: Int = 0,

    val teams: List<String> = emptyList(),

    @ServerTimestamp
    val creationDate: Date? = null
) {
    constructor() : this(
        id = "",
        name = "",
        description = "",
        date = "",
        place = "",
        image = "",
        organizer = "",
        creatorId = "",
        prizePool = "",
        entryTax = "",
        teamsNum = 0,
        teams = emptyList(),
        creationDate = null
    )
}