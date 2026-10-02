package com.creacionesnormita.mobile.core.model

data class ItemCotizacion(
    val producto: Producto,
    val talla: Talla,
    val cantidad: Int = 1,
)