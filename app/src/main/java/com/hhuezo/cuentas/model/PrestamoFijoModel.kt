package com.hhuezo.cuentas.model

data class PrestamoFijoResponse(
    val success: Boolean,
    val message: String?,
    val data: List<PrestamoFijo>?
)

data class PrestamoFijo(
    val id: Int?,
    val codigo: String?,
    val cantidad: Double?,
    val interes: Double?,
    val estado: Int?,
    val amortizacion: Double?,
    val comprobante: String?,
    val administrador: String?,
    val pagoEspecifico: Boolean?,
    val persona: String?,
    val tipoPago: String?,
    val tipoPagoId: Int?,
    val numeroPagos: Int?,
    val observacion: String?,
    val fecha: String?,
    val deuda: Double?,
    val cuota: Double?
)


data class PrestamoFijoCreateResponse(
    val success: Boolean?,
    val message: String?,
    val data: List<Persona>?
)

data class PrestamoFijoCreatePersona(
    val id: Int?,
    val nombre: String?
)

