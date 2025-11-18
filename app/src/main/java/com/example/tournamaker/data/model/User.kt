package com.example.tournamaker.data.model

data class User(
    val id: String = "",
    val username: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val avatar: String = ""
) {
    constructor() : this("", "", "", "", "", "")
}
