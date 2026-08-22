package com.creacionesnormita.mobile.core.sample

import com.creacionesnormita.mobile.core.model.ServicioDestacado
import com.creacionesnormita.mobile.core.model.Vestido

val vestidoDestacado = Vestido(
    id = 1,
    nombre = "Vestido del día",
    estado = "Disponible ahora",
    precio = "Desde $95"
)

val vestidos = listOf(
    Vestido(2, "Vestido Aurora", "Disponible ahora", "Desde $110", true),
    Vestido(3, "Vestido Luna", "A tu medida", "Cotizar"),
    Vestido(4, "Vestido Gala", "Disponible ahora", "Desde $140"),
    Vestido(5, "Vestido Rosa", "A tu medida", "Cotizar", true),
    Vestido(6, "Vestido Celeste", "Disponible ahora", "Desde $125"),
    Vestido(7, "Vestido Perla", "A tu medida", "Cotizar")
)

val serviciosDestacados = listOf(
    ServicioDestacado("Asesoría", "Te ayudamos a elegir"),
    ServicioDestacado("Taller", "Ajustes y confecciones"),
    ServicioDestacado("Envíos", "Nacional e internacional")
)
