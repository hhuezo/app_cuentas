package com.hhuezo.cuentas.model

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hhuezo.cuentas.R

class PagoMensualAdapter(private val pagos: List<Pago>, private val listener: OnPagoMensualClickListener) : RecyclerView.Adapter<PagoMensualAdapter.PagoViewHolder>() {


    interface OnPagoMensualClickListener {
        fun onReportePagoClick(id: Int)
    }

    class PagoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
        val cantidadTextView: TextView = itemView.findViewById(R.id.cantidadTextView)
        val fechaTextView: TextView = itemView.findViewById(R.id.fechaTextView)
        val colorView: View = itemView.findViewById(R.id.colorView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pago_item, parent, false)
        return PagoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PagoViewHolder, position: Int) {
        val pago = pagos[position]
        holder.nombreTextView.text = pago.nombre
        holder.cantidadTextView.text = "$"+pago.cantidad
        holder.fechaTextView.text = pago.fecha_formato


        if (pago.pagado == 1) {
            holder.colorView.setBackgroundColor(Color.parseColor("#4CAF50"))
        }
        else{
            holder.colorView.setBackgroundColor(Color.parseColor("#FFA500"))
        }


    }

    override fun getItemCount(): Int {
        return pagos.size
    }
}