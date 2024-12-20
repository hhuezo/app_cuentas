package com.hhuezo.cuentas.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hhuezo.cuentas.R

class PersonaAdapter(
    private val personas: List<Persona>,
    private val listener: OnPersonaClickListener
) : RecyclerView.Adapter<PersonaAdapter.PersonaViewHolder>() {

    interface OnPersonaClickListener {
        fun onPersonaClick(id: Int)
    }

    inner class PersonaViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                val persona = personas[adapterPosition]
                listener.onPersonaClick(persona.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.persona_item, parent, false)
        return PersonaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonaViewHolder, position: Int) {
        val persona = personas[position]

        // Asegúrate de tener los TextViews correspondientes en tu diseño de persona_item
        val nombreTextView: TextView = holder.view.findViewById(R.id.nombreTextView)
        val telefonoTextView: TextView = holder.view.findViewById(R.id.telefonoTextView)


        // Asigna los datos de la persona a los elementos de la vista
        nombreTextView.text = persona.nombre
        telefonoTextView.text = persona.telefono ?: ""
    }

    override fun getItemCount() = personas.size
}