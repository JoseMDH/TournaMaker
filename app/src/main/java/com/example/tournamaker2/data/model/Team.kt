package com.example.tournamaker2.data.model

data class Team(
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val participants: List<String> = emptyList(),
    val creador: String = "",
    val torneos: List<String> = emptyList()
) {
    constructor() : this("", "", "", emptyList(), "", emptyList())
}
