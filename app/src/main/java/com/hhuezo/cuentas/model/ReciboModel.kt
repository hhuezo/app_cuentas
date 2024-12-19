package com.hhuezo.cuentas.model

data class Recibo(
    val id: Int,
    val fecha: String,
    val cantidad: String,
    val comprobante: String?,
    val interes: String,
    val remanente: String,
    val estado: Int,
    val tipo: Int,
    val observacion: String
)

data class ReciboCreateResponse(
    val success: Boolean,
    val message: String,
    val data: ReciboCreate
)

data class ReciboCreate(
    val nombre: String,
    val remanente: String,
    val interes: String?,
    val total: String
)