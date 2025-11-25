package com.example.tournamaker.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// Modelo de datos para un Partido. Contiene todos los campos necesarios.

data class Match(
    val id: String = "",
    val name: String = "", // Nombre general del partido (ej. "Derbi Sub-18")
    val image: String = "", // URL de la imagen para la tarjeta del partido
    val tournamentId: String = "",
    val team1Name: String = "",
    val team2Name: String = "",
    val team1Score: Int = 0,
    val team2Score: Int = 0,
    val date: String = "",
    val hour: String = "",
    val status: String = "PENDING", // PENDING, IN_PROGRESS, FINISHED

    @ServerTimestamp
    val creationDate: Date? = null
) {
    // Constructor vacío requerido por Firestore
    constructor() : this(
        id = "",
        name = "",
        image = "",
        tournamentId = "",
        team1Name = "",
        team2Name = "",
        team1Score = 0,
        team2Score = 0,
        date = "",
        hour = "",
        status = "PENDING",
        creationDate = null
    )
}