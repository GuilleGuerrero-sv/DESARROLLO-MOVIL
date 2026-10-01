package com.creacionesnormita.mobile.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Perfil(
    val id: String,
    val nombre: String,
    val fecha_nacimiento: String,
    val celular: String,
    val otro_contacto: String? = null,
    val foto_url: String? = null,
    val rol: String = ROL_CLIENTE
) {
    companion object {
        const val ROL_CLIENTE = "Cliente"
        const val ROL_EMPLEADO = "Empleado"
        const val ROL_ADMINISTRADOR = "Administrador"
    }

    val esEmpleado: Boolean get() = rol.equals(ROL_EMPLEADO, ignoreCase = true) || rol.equals(ROL_ADMINISTRADOR, ignoreCase = true) || rol.equals("admin", ignoreCase = true) || rol.equals("empleado", ignoreCase = true)
    val esAdmin: Boolean get() = rol.equals(ROL_ADMINISTRADOR, ignoreCase = true) || rol.equals("admin", ignoreCase = true)
}
