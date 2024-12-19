package com.hhuezo.cuentas.model

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("gananciasPrestamo") val gananciasPrestamo: List<Float>?,
    @SerializedName("gananciasPrestamoMes") val gananciasPrestamoMes: List<String>?,
    @SerializedName("gananciasReciboFijo") val gananciasReciboFijo: List<Float>?,
    @SerializedName("gananciasReciboFijoMes") val gananciasReciboFijoMes: List<String>?,
    val dataGeneral: DataGeneral?
)


data class DataGeneral(
    val countPrestamos: Int,
    val totalPrestado: String,
    val dineroInvertido: String,
    val totalCargos: String,
    val totalReintegrado: String,
    val totalInteresReintegrado: String,
    val totalFijoReintegrado: String
)
