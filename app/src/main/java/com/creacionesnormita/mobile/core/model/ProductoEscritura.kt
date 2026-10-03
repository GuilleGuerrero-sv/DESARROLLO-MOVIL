package com.creacionesnormita.mobile.core.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO usado SOLO para crear/actualizar un producto. A diferencia de [Producto],
 * no incluye `id` (lo genera la base de datos) ni `stockPorTalla` (es una relación
 * embebida de solo lectura, no una columna real de la tabla `producto`).
 */
/**
 * IMPORTANTE: supabase-kt serializa con `encodeDefaults = false`, o sea que cualquier campo cuyo
 * valor sea igual al valor por defecto NO se envía. Por eso `disponible = true` nunca llegaba a la
 * base de datos (el vestido se podía ocultar pero no volver a mostrar). @EncodeDefault obliga a
 * enviar esos campos siempre. `categoria` se deja sin la anotación a propósito, así al editar no se pisa.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ProductoEscritura(
    val nombre: String,
    @EncodeDefault val descripcion: String? = null,
    val precio: Double,
    val categoria: String = "vestido_15",
    @EncodeDefault val imagenes: List<String> = emptyList(),
    @EncodeDefault val color: String? = null,
    @EncodeDefault val disponible: Boolean = true,
)

@Serializable
data class ProductoStockEscritura(
    @SerialName("producto_id")
    val productoId: Int,
    val talla: Talla,
    val stock: Int,
)

@Serializable
internal data class SoloDisponible(val disponible: Boolean)

@Serializable
internal data class SoloDestacado(val destacado: Boolean)

@Serializable
internal data class SoloActivo(val activo: Boolean)

@Serializable
internal data class SoloRol(val rol: String)