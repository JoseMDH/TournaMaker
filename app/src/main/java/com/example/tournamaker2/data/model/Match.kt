package com.example.tournamaker2.data.model

data class Match(
    val id: String = "",
    val date: String = "",
    val description: String? = null,
    val entryTax: String? = null,
    val hour: String = "",
    val image: String? = null,
    val name: String? = null,
    val organizer: String = "",
    val teams: List<String> = emptyList(),
    val participantsNum: Int? = 0,
    val place: String = "",
    val prizePool: String? = null,
    val tournament: String? = null,
    val participants: List<String> = emptyList(),
    val matchState: MatchState = MatchState()
) {
    constructor() : this("", "", null, null, "",
        null, null, "", emptyList(), 0,
        "", null, null, emptyList(), MatchState()
    )
}

data class MatchState(
    val state: String = "",
    val scoreA: Int = 0,
    val scoreB: Int = 0
)
