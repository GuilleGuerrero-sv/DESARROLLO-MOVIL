package com.creacionesnormita.mobile.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Talla {
    XS, S, M, L, XL, XXL
}

@Serializable
data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String? = null,
    val precio: Double,
    val categoria: String = "vestido_15",
    val imagenes: List<String> = emptyList(), // URLs de Cloudinary, orden = orden del carrusel
    val color: String? = null,
    val disponible: Boolean = true,
    val destacado: Boolean = false,
    // Relación embebida: solo se llena si la consulta la incluye explícitamente
    @SerialName("producto_stock")
    val stockPorTalla: List<ProductoStock> = emptyList(),
)

@Serializable
data class ProductoStock(
    val id: Int,
    @SerialName("producto_id")
    val productoId: Int,
    val talla: Talla,
    val stock: Int,
)