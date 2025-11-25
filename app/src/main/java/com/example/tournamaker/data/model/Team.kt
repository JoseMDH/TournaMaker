package com.example.tournamaker.data.model

import com.google.firebase.firestore.PropertyName

data class Team(
    val id: String = "",
    val name: String = "",
    val image: String = "",

    @get:PropertyName("creador") @set:PropertyName("creador")
    var creatorId: String = "",

    val participants: List<String> = emptyList(),
    val torneos: List<String> = emptyList()
) {
    constructor() : this(
        id = "",
        name = "",
        image = "",
        creatorId = "",
        participants = emptyList(),
        torneos = emptyList()
    )
}