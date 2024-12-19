package com.hhuezo.cuentas.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hhuezo.cuentas.R

class PrestamoFinalizadoAdapter(private val prestamos: List<PrestamoFinalizado>) : RecyclerView.Adapter<PrestamoFinalizadoAdapter.PrestamoFinalizadoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrestamoFinalizadoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_prestamo_finalizado, parent, false)
        return PrestamoFinalizadoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrestamoFinalizadoViewHolder, position: Int) {
        val prestamo = prestamos[position]
        holder.bind(prestamo)
    }

    override fun getItemCount(): Int = prestamos.size

    class PrestamoFinalizadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textNombre: TextView = itemView.findViewById(R.id.text_nombre)
        private val textFecha: TextView = itemView.findViewById(R.id.text_fecha)
        private val textCantidad: TextView = itemView.findViewById(R.id.text_cantidad)
        private val textNumeroPagos: TextView = itemView.findViewById(R.id.text_numero_pagos)
        private val textCantidadRecibo: TextView = itemView.findViewById(R.id.text_cantidad_recibo)
        private val textTipo: TextView = itemView.findViewById(R.id.text_tipo)

        fun bind(prestamo: PrestamoFinalizado) {
            textNombre.text = prestamo.nombre
            textFecha.text = prestamo.fecha
            textCantidad.text = "Cantidad $"+prestamo.cantidad
            textNumeroPagos.text = "Pagos :"+prestamo.numeroPagos.toString()
            textCantidadRecibo.text = "Cuota $"+prestamo.cantidadRecibo
            textTipo.text = prestamo.tipo
        }
    }
}
