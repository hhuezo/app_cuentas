package com.hhuezo.cuentas.model

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hhuezo.cuentas.R

class PrestamoAdapter(
    private var prestamos: List<Prestamo>,
    private val listener: OnPrestamoClickListener
) : RecyclerView.Adapter<PrestamoAdapter.PrestamoViewHolder>(), Filterable {

    private var prestamosFiltered: List<Prestamo> = prestamos

    interface OnPrestamoClickListener {
        fun onPrestamoClick(id: Int)
    }

    inner class PrestamoViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrestamoViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.prestamo_item, parent, false)
        return PrestamoViewHolder(v)
    }

    override fun onBindViewHolder(holder: PrestamoViewHolder, position: Int) {
        val prestamo: Prestamo = prestamosFiltered[position]
        val cantidadTextView = holder.itemView.findViewById<TextView>(R.id.cantidadTextView)
        val interesTextView = holder.itemView.findViewById<TextView>(R.id.interesTextView)
        val personaTextView = holder.itemView.findViewById<TextView>(R.id.personaTextView)
        val tipoPagoTextView = holder.itemView.findViewById<TextView>(R.id.tipoPagoTextView)
        val fechaTextView = holder.itemView.findViewById<TextView>(R.id.fechaTextView)
        val observacionTextView = holder.itemView.findViewById<TextView>(R.id.observacionTextView)
        val estadoTextView = holder.itemView.findViewById<TextView>(R.id.estadoTextView)
        val codigoTextView = holder.itemView.findViewById<TextView>(R.id.codigoTextView)
        val cuotaTextView = holder.itemView.findViewById<TextView>(R.id.cuotaTextView)
        val deudaTextView = holder.itemView.findViewById<TextView>(R.id.deudaTextView)
        val colorView: View = holder.itemView.findViewById(R.id.colorView)
        //val cardView: CardView = holder.itemView.findViewById(R.id.cardView)

        cantidadTextView.text = "$" + prestamo.cantidad.toString()
        codigoTextView.text = "Codigo: " + prestamo.codigo.toString()
        interesTextView.text = prestamo.interes.toString() + "%"
        personaTextView.text = prestamo.persona
        tipoPagoTextView.text = "Tipo pago: " + prestamo.tipoPago
        fechaTextView.text = prestamo.fecha
        estadoTextView.text = if (prestamo.estado == 1) "Activo" else "Finalizado"
        cuotaTextView.text = "Cuota: $" + prestamo.cuota
        deudaTextView.text = "Deuda: $" + prestamo.deuda
        observacionTextView.text = prestamo.observacion

        holder.itemView.setOnClickListener {
            listener.onPrestamoClick(prestamo.id)
        }


        if (prestamo.estado == 1) {
            colorView.setBackgroundColor(Color.parseColor("#FFA500"))
        }

        else if (prestamo.estado == 2) {
            colorView.setBackgroundColor(Color.parseColor("#4CAF50"))
        }



    }

    override fun getItemCount(): Int {
        return prestamosFiltered.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<Prestamo>()
                if (constraint.isNullOrEmpty()) {
                    filteredList.addAll(prestamos)
                } else {
                    val filterPattern = constraint.toString().toLowerCase().trim()
                    prestamos.forEach { prestamo ->
                        if (prestamo.persona.toLowerCase().contains(filterPattern)) {
                            filteredList.add(prestamo)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                prestamosFiltered = results?.values as? List<Prestamo> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}