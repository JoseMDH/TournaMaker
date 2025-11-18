package com.example.tournamaker2.data.model

data class Team(
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val participants: List<String> = emptyList(),
    val creator: String = "",
    val tournaments: List<String> = emptyList()
) {
    constructor() : this("", "", "", emptyList(), "",
        emptyList())
}
