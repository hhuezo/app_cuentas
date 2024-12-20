package com.hhuezo.cuentas.model

data class CargoCreateResponse(
    val success: Boolean,
    val message: String,
    val data: CargoData
)

data class CargoData(
    val id: Int,
    val codigo: String,
    val nombre: String
)