package com.hhuezo.cuentas.model

data class Prestamo(
    val id: Int,                       // No puede ser nulo
    val cantidad: String?,             // Puede ser nulo
    val interes: String,               // Cambiado a String
    val estado: Int?,                  // Puede ser nulo
    val amortizacion: String?,         // Puede ser nulo
    val comprobante: String?,          // Puede ser nulo
    val administrador: String?,        // Cambiado a String
    val pagoEspecifico: String?,       // Puede ser nulo
    val persona: String,               // No puede ser nulo
    val tipoPago: String?,             // Puede ser nulo
    val fecha: String?,                // Puede ser nulo
    val observacion: String?,          // Puede ser nulo
    val codigo: String?,               // Puede ser nulo
    val cuota: String?,                // Puede ser nulo
    val deuda: String?                 // Puede ser nulo
)






data class PrestamoResponse(
    val success: Boolean,
    val message: String,
    val data: List<Prestamo>
)

data class PrestamoEditResponse(
    val success: Boolean,
    val message: String,
    val data: Prestamo
)



data class PrestamoCreateResponse(
    val success: Boolean,
    val message: String,
    val data: Data
)

data class Data(
    val personas: List<Solicitante>,
    val usuarios: List<Usuario>,
    val tipos_pago: List<TipoPago>
)

data class Solicitante(val id: Int, val nombre: String)
data class Usuario(val id: Int, val username: String)
data class TipoPago(val id: Int, val nombre: String)


data class PrestamoShowResponse(
    val success: Boolean,
    val message: String,
    val data: DataShow
)

data class DataShow(
    val prestamo: PrestamoShow,
    val recibos: List<Recibo>
)

data class PrestamoShow(
    val id: Int,
    val codigo: String,
    val persona: String,
    val cantidad: String,
    val interes: Int,
    val fecha: String,
    val tipo: String,
    val estado: Int,
    val pago_especifico: String,
    val remanente: String,
    val comprobante: String?
)