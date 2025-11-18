package com.example.tournamaker2.data.model

data class Tournament(
    val id: String = "",
    val name: String = "",
    val date: String? = null,
    val place: String? = null,
    val organizer: String? = null,
    val image: String? = null,
    val description: String? = null,
    val entryTax: String? = null,
    val teams: List<String>? = emptyList(),
    val prizePool: String? = null,
    val teamsNum: Int? = 0
) {
    constructor() : this("", "", null, null, null,
        null, null, null, emptyList(), null,
        0)
}
