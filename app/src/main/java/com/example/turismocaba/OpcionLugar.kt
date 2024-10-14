package com.example.turismocaba

enum class OpcionTipo {
    UBICACION,
    FOTO,
    CALENDARIO,
    SELECCIONAR_IMAGEN
}

data class OpcionLugar(
    val nombre: String,
    val icono: Int,
    val tipo: OpcionTipo
)