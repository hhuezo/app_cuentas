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

class PrestamoFijoAdapter(
    private var prestamosFijos: List<PrestamoFijo>,
    private val listener: OnPrestamoFijoClickListener
) : RecyclerView.Adapter<PrestamoFijoAdapter.PrestamoFijoViewHolder>(), Filterable {

    private var prestamosFijosFiltered: List<PrestamoFijo> = prestamosFijos

    interface OnPrestamoFijoClickListener {
        fun onPrestamoFijoClick(id: Int)
    }

    inner class PrestamoFijoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cantidadTextView: TextView = view.findViewById(R.id.cantidadTextView)
        val personaTextView: TextView = view.findViewById(R.id.personaTextView)
        val fechaTextView: TextView = view.findViewById(R.id.fechaTextView)
        val estadoTextView: TextView = view.findViewById(R.id.estadoTextView)
        val codigoTextView: TextView = view.findViewById(R.id.codigoTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrestamoFijoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.prestamo_fijo_item, parent, false)
        return PrestamoFijoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrestamoFijoViewHolder, position: Int) {
        val prestamoFijo = prestamosFijosFiltered[position]

        holder.cantidadTextView.text = "Cantidad: "+prestamoFijo.cantidad?.let { "$$it" } ?: "N/A"
        holder.personaTextView.text = prestamoFijo.persona ?: "N/A"
        holder.fechaTextView.text = prestamoFijo.fecha ?: "N/A"
        holder.codigoTextView.text = prestamoFijo.codigo
        val colorView: View = holder.itemView.findViewById(R.id.colorView)
        holder.estadoTextView.text = when (prestamoFijo.estado) {
            1 -> "Activo"
            2 -> "Finalizado"
            else -> "Desconocido"
        }

        holder.itemView.setOnClickListener {
            prestamoFijo.id?.let { it1 -> listener.onPrestamoFijoClick(it1) }
        }


        if (prestamoFijo.estado == 1) {
            colorView.setBackgroundColor(Color.parseColor("#FFA500"))
        }

        else if (prestamoFijo.estado == 2) {
            colorView.setBackgroundColor(Color.parseColor("#4CAF50"))
        }
    }

    override fun getItemCount(): Int {
        return prestamosFijosFiltered.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<PrestamoFijo>()
                if (constraint.isNullOrEmpty()) {
                    filteredList.addAll(prestamosFijos)
                } else {
                    val filterPattern = constraint.toString().toLowerCase().trim()
                    prestamosFijos.forEach { prestamoFijo ->
                        if (prestamoFijo.persona?.toLowerCase()?.contains(filterPattern) == true) {
                            filteredList.add(prestamoFijo)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                prestamosFijosFiltered = results?.values as? List<PrestamoFijo> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}
