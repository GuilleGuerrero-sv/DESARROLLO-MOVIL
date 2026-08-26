package com.creacionesnormita.mobile.core.model

data class Vestido(
    val id: Int,
    val nombre: String,
    val estado: String,
    val precio: String,
    val esFavorito: Boolean = false
)

data class ServicioDestacado(
    val titulo: String,
    val descripcion: String
)
