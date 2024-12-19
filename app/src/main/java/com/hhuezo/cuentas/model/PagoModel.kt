package com.hhuezo.cuentas.model

data class ResponsePagoData(
    val success: Boolean?,
    val message: String?,
    val data: DataPago?
)

data class DataPago(
    val pagos: List<Pago>?
)

data class Pago(
    val id: Int?,
    val prestamo_id: Int?,
    val fecha: String?,
    val fecha_formato: String?,
    val cantidad: String?,
    val pagado: Int?,
    val nombre: String?
)