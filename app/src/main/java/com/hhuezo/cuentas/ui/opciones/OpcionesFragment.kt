package com.hhuezo.cuentas.ui.opciones

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.hhuezo.cuentas.R
import com.hhuezo.cuentas.databinding.FragmentOpcionesBinding
import com.hhuezo.cuentas.databinding.FragmentPrestamoBinding
import com.hhuezo.cuentas.ui.prestamo.PrestamoFragmentDirections


class OpcionesFragment : Fragment() {

    private var _binding: FragmentOpcionesBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Más opciones"
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_opciones, container, false)

        _binding = FragmentOpcionesBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Más opciones"

        val cardDashboard: CardView = view.findViewById(R.id.card_dashboard)
        cardDashboard.setOnClickListener {
            // Redireccion
            val action = OpcionesFragmentDirections.actionOpcionesFragmentToDashboardFragment()
            findNavController().navigate(action)
        }

        val cardPersona: CardView = view.findViewById(R.id.card_personas)
        cardPersona.setOnClickListener {
            // Redireccion
            val action = OpcionesFragmentDirections.actionOpcionesFragmentToPersonaFragment()
            findNavController().navigate(action)
        }


        val cardPrestamoFijo: CardView = view.findViewById(R.id.card_prestamo_fijo)
        cardPrestamoFijo.setOnClickListener {
            // Redireccion
            val action = OpcionesFragmentDirections.actionOpcionesFragmentToPrestamoFijoFragment()
            findNavController().navigate(action)
        }





    }

}