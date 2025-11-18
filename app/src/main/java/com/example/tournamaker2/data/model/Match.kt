package com.example.tournamaker2.data.model

data class Match(
    val id: String = "",
    val date: String = "",
    val description: String? = null,
    val entry_tax: String? = null,
    val hour: String = "",
    val image: String? = null,
    val name: String? = null,
    val organizer: String = "",
    val teams: List<String> = emptyList(),
    val participants_num: Int? = 0,
    val place: String = "",
    val prize_pool: String? = null,
    val tournament: String? = null,
    val participants: List<String> = emptyList(),
    val estadoPartido: EstadoPartido = EstadoPartido()
) {
    constructor() : this("", "", null, null, "", null, null, "", emptyList(), 0, "", null, null, emptyList(), EstadoPartido())
}

data class EstadoPartido(
    val estado: String = "",
    val marcadorA: Int = 0,
    val marcadorB: Int = 0
)
