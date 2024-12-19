package com.hhuezo.cuentas.model

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("gananciasPrestamo") val gananciasPrestamo: List<Float>,
    @SerializedName("gananciasPrestamoMes") val gananciasPrestamoMes: List<String>
)
