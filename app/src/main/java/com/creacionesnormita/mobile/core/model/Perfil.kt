package com.creacionesnormita.mobile.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Perfil(
    val id: String,
    val nombre: String,
    val fecha_nacimiento: String,
    val celular: String,
    val otro_contacto: String? = null
)
