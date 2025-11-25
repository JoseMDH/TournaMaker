package com.example.tournamaker.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val username: String = "",
    val avatar: String = ""
   ) {
    constructor() : this(
        id = "",
        name = "",
        email = "",
        username = "",
        avatar = ""
    )
}