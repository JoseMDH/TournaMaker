package com.example.tournamaker.data.model

data class Team(
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val creatorId: String = "",
    val participants: List<String> = emptyList()
) {
    constructor() : this(
        id = "",
        name = "",
        image = "",
        creatorId = "",
        participants = emptyList()
    )
}