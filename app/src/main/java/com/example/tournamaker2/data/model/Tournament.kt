package com.example.tournamaker2.data.model

data class Tournament(
    val id: String = "",
    val name: String = "",
    val date: String? = null,
    val place: String? = null,
    val organizer: String? = null,
    val image: String? = null,
    val description: String? = null,
    val entry_tax: String? = null,
    val teams: List<String>? = emptyList(),
    val prize_pool: String? = null,
    val teams_num: Int? = 0
) {
    constructor() : this("", "", null, null, null, null, null, null, emptyList(), null, 0)
}
