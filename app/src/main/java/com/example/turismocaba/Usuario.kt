package com.example.turismocaba

data class Usuario(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val email: String,
    val country: String,
    val password: String,
    val favoriteTeam: String?,
    val favoriteBook: String?
)
