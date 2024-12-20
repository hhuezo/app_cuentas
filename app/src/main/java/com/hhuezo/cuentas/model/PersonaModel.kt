package com.hhuezo.cuentas.model

data class Persona(
    val id: Int,
    val nombre: String,
    val documento: String?,
    val activo: String,
    val telefono: String?,
    val banco: String?,
    val cuenta: String?
)

data class PersonaResponse(
    val success: Boolean,
    val message: String,
    val data: List<Persona>
)

data class PersonaEditResponse(
    val success: Boolean,
    val message: String,
    val data: Persona
)