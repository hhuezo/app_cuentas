package com.hhuezo.cuentas.model

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.hhuezo.cuentas.R

class ReciboAdapter(private val recibos: List<Recibo>, private val listener: OnReciboClickListener) : RecyclerView.Adapter<ReciboAdapter.ReciboViewHolder>() {

    interface OnReciboClickListener {
        fun onReciboClick(id: Int)
    }

    inner class ReciboViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                val recibo = recibos[adapterPosition]
                listener.onReciboClick(recibo.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReciboViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recibo_item, parent, false)
        return ReciboViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReciboViewHolder, position: Int) {
        val recibo = recibos[position]

        val cardView: CardView = holder.view.findViewById(R.id.cardView)
        //val imageView: ImageView = holder.view.findViewById(R.id.imageView)

        // Asegúrate de tener los TextViews correspondientes en tu diseño de recibo_item
        val cantidadTextView: TextView = holder.view.findViewById(R.id.cantidadTextView)
        val fechaTextView: TextView = holder.view.findViewById(R.id.fechaTextView)
        val observacionTextView: TextView = holder.view.findViewById(R.id.observacionTextView)
        val colorView: View = holder.itemView.findViewById(R.id.colorView)
        val remanenteTextView: TextView = holder.view.findViewById(R.id.remanenteTextView)
        val interesTextView: TextView = holder.view.findViewById(R.id.interesTextView)



        // Asigna los datos del recibo a los elementos de la vista
        fechaTextView.text = recibo.fecha
        cantidadTextView.text = "$"+recibo.cantidad
        if (recibo.observacion.isNotEmpty()) {
            observacionTextView.text = recibo.observacion
            observacionTextView.visibility = View.VISIBLE
        } else {
            observacionTextView.visibility = View.GONE
        }

        remanenteTextView.text = "Remanente: $"+recibo.remanente
        interesTextView.text = "Interes: $"+recibo.interes

        if (recibo.tipo == 1)
        {
            colorView.setBackgroundColor(Color.parseColor("#4CAF50"))
        }
        else{
            colorView.setBackgroundColor(Color.parseColor("#C8102E"))
        }


    }

    override fun getItemCount() = recibos.size
}
