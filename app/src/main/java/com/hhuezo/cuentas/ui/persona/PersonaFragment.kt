package com.hhuezo.cuentas.ui.persona

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import com.hhuezo.cuentas.databinding.FragmentPersonaBinding
import com.hhuezo.cuentas.model.HttpClient
import com.hhuezo.cuentas.model.Persona
import com.hhuezo.cuentas.model.PersonaAdapter
import com.hhuezo.cuentas.model.PersonaResponse
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class PersonaFragment : Fragment(), PersonaAdapter.OnPersonaClickListener {

    private var _binding: FragmentPersonaBinding? = null
    private val binding get() = _binding!!

    private lateinit var personaAdapter: PersonaAdapter
    private var personas: List<Persona> = emptyList()

    private val client by lazy { HttpClient(requireContext()) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPersonaBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Personas"

        val personasRecyclerView = view.findViewById<RecyclerView>(R.id.RecyclerView)
        personasRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = PersonaAdapter(emptyList(), this)
        personasRecyclerView.adapter = adapter


        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.loadingProgressBar)
        loadingProgressBar.visibility = View.VISIBLE


        val fab: FloatingActionButton = view.findViewById(R.id.fab)

        binding.fab.setOnClickListener {
            // Navegar al PersonaCreateFragment
            val action =  PersonaFragmentDirections.actionPersonaFragmentToPersonaCreateFragment()
            findNavController().navigate(action)

        }



        client.get("persona", object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API_ERROR", "Fallo al obtener los datos: ${e.message}")
                loadingProgressBar.visibility = View.GONE // Ocultar el ProgressBar en caso de error
            }

            override fun onResponse(call: Call, response: Response) {
                if (isAdded) {
                    val responseData = response.body?.string()

                    if (responseData != null) {
                        Log.d("API_RESPONSE", responseData)

                        val gson = Gson()
                        val personaResponse =
                            gson.fromJson(responseData, PersonaResponse::class.java)
                        val personas = personaResponse?.data ?: emptyList()

                        requireActivity().runOnUiThread {
                            personaAdapter = PersonaAdapter(personas, this@PersonaFragment)
                            personasRecyclerView.adapter = personaAdapter
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
            }
        })


        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()) {
                    // Realizar la solicitud a la API con el término de búsqueda
                    val url = "persona?search=$newText"
                    client.get(url, object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.e("API_ERROR", "Fallo al obtener los datos: ${e.message}")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            if (isAdded) {
                                val responseData = response.body?.string()
                                if (responseData != null) {
                                    Log.d("API_RESPONSE", responseData)

                                    val gson = Gson()
                                    val personaResponse = gson.fromJson(responseData, PersonaResponse::class.java)
                                    val personas = personaResponse?.data ?: emptyList()

                                    requireActivity().runOnUiThread {
                                        personaAdapter = PersonaAdapter(personas, this@PersonaFragment)
                                        personasRecyclerView.adapter = personaAdapter
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
                        }
                    })
                }
                return true
            }
        })

        // Registrar el callback de entrada de métodos de entrada (IME)
        searchView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    override fun onPersonaClick(id: Int) {
        val action = PersonaFragmentDirections.actionPersonaFragmentToPersonaEditFragment(id)
        findNavController().navigate(action)

    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

}