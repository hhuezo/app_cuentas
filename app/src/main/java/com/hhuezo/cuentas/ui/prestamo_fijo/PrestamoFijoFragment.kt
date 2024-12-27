package com.hhuezo.cuentas.ui.prestamo_fijo

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.hhuezo.cuentas.R
import com.hhuezo.cuentas.databinding.FragmentPrestamoFijoBinding
import com.hhuezo.cuentas.model.HttpClient
import com.hhuezo.cuentas.model.PrestamoFijo
import com.hhuezo.cuentas.model.PrestamoFijoAdapter
import com.hhuezo.cuentas.model.PrestamoFijoResponse
import com.hhuezo.cuentas.ui.prestamo.PrestamoFragmentDirections
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


class PrestamoFijoFragment : Fragment() , PrestamoFijoAdapter.OnPrestamoFijoClickListener {


    private val client by lazy { HttpClient(requireActivity()) }

    private var _binding: FragmentPrestamoFijoBinding? = null
    private val binding get() = _binding!!
    var userId = "1"
    var rolId = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentPrestamoFijoBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Préstamos fijo"

        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.loadingProgressBar)
        loadingProgressBar.visibility = View.VISIBLE


        val fab: FloatingActionButton = view.findViewById(R.id.fab)

        if (rolId != "1") {
            fab.visibility = View.GONE
        }

        binding.fab.setOnClickListener {
            // Navegar al PrestamoCreateFragment
            val action = PrestamoFijoFragmentDirections.actionPrestamoFijoFragmentToPrestamoFijoCreateFragment()
            findNavController().navigate(action)
        }


        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = PrestamoFijoAdapter(emptyList(), this)
        recyclerView.adapter = adapter

        Log.d("url","prestamo_fijo?rol=$rolId&id_usuario=$userId")
        client.get("prestamo_fijo?rol=$rolId&id_usuario=$userId", object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API_ERROR", "Fallo al obtener los datos: ${e.message}")
                requireActivity().runOnUiThread {
                    loadingProgressBar.visibility =
                        View.GONE // Ocultar el ProgressBar en caso de error
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                if (responseData != null) {
                    Log.d("API_RESPONSE", responseData)

                    val gson = Gson()
                    val prestamoResponse = gson.fromJson(responseData, PrestamoFijoResponse::class.java)

                    val prestamos = prestamoResponse?.data ?: emptyList()

                    requireActivity().runOnUiThread {
                        recyclerView.adapter =
                            PrestamoFijoAdapter(prestamos, this@PrestamoFijoFragment)
                        loadingProgressBar.visibility =
                            View.GONE // Ocultar el ProgressBar una vez que los datos se hayan cargado
                    }
                } else {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "No hay datos: ${response.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }


        })


        val searchView = view.findViewById<SearchView>(R.id.searchView)


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Aquí puedes realizar la búsqueda con el texto ingresado en el SearchView
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Aquí puedes reaccionar a cambios en el texto de búsqueda
                // Por ejemplo, puedes filtrar los datos en tu RecyclerView
                (recyclerView.adapter as? PrestamoFijoAdapter)?.filter?.filter(newText)
                return true
            }
        })

    }

    override fun onPrestamoFijoClick(id: Int) {
        val action = PrestamoFijoFragmentDirections.actionPrestamoFijoFragmentToPrestamoFijoShowFragment(id)
        findNavController().navigate(action)
    }

}