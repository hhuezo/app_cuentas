package com.hhuezo.cuentas.ui.persona

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.hhuezo.cuentas.R
import com.hhuezo.cuentas.databinding.FragmentPersonaEditBinding
import com.hhuezo.cuentas.model.HttpClient
import com.hhuezo.cuentas.model.Persona
import com.hhuezo.cuentas.model.PersonaEditResponse
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class PersonaEditFragment : Fragment() {

    private var _binding: FragmentPersonaEditBinding? = null
    private val binding get() = _binding!!

    private val client by lazy { HttpClient(requireContext()) }
    private var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPersonaEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Modificar personas"

        //opteniendo el id del fragment anterior
        id = arguments?.getInt("id") ?: 0


        val URL = getString(R.string.url)



        val nombreEditText = view.findViewById<EditText>(R.id.nombreEditText)
        val documentoEditText = view.findViewById<EditText>(R.id.documentoEditText)
        val telefonoEditText = view.findViewById<EditText>(R.id.telefonoEditText)
        val bancoEditText = view.findViewById<EditText>(R.id.bancoEditText)
        val cuentaEditText = view.findViewById<EditText>(R.id.cuentaEditText)

        val client = OkHttpClient()
        Log.d("TAG", "$URL/persona/$id/edit")


        client.newCall(
            Request.Builder()
                .url(getString(R.string.url)+"persona/$id/edit")
                .build()
        )
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("API_ERROR", "Fallo al obtener los datos: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()

                    // Imprime la respuesta en la consola de registro
                    if (responseData != null) {
                        Log.d("API_RESPONSE", responseData)
                    }

                    // Utiliza Gson para convertir la respuesta JSON en una lista de personas
                    val gson = Gson()
                    val personaEditResponse =
                        gson.fromJson(responseData, PersonaEditResponse::class.java)
                    val persona = personaEditResponse.data

                    // Actualiza los controles en el hilo principal
                    requireActivity().runOnUiThread {
                        nombreEditText.setText(persona.nombre)
                        documentoEditText.setText(persona.documento)
                        telefonoEditText.setText(persona.telefono)
                        bancoEditText.setText(persona.banco)
                        cuentaEditText.setText(persona.cuenta)
                    }
                }
            })




        val aceptarButton: Button = view.findViewById(R.id.aceptarButton)

        aceptarButton.setOnClickListener {
            val client_put = HttpClient(requireContext()) // Usar tu clase HttpClient
            val persona = Persona(
                id = id,
                nombre = nombreEditText.text.toString(),
                documento = documentoEditText.text.toString(),
                activo = "1",
                telefono = telefonoEditText.text.toString(),
                banco = bancoEditText.text.toString(),
                cuenta = cuentaEditText.text.toString()
            )

            val gson = Gson()
            val personaJson = gson.toJson(persona)

            client_put.put("persona/$id", personaJson, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("API_ERROR", "Fallo al actualizar los datos: ${e.message}")
                    requireActivity().runOnUiThread {
                        val dialog = MaterialDialog(requireContext()).show {
                            title(text = "Error")
                            message(text = "Fallo al actualizar los datos: ${e.message}")
                            icon(R.drawable.baseline_error_24) // Cambiar a tu icono de error
                            positiveButton(text = "Aceptar")
                        }
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()

                    // Imprime la respuesta en la consola de registro
                    if (responseData != null) {
                        Log.d("API_RESPONSE", responseData)
                    }

                    requireActivity().runOnUiThread {
                        val dialog = MaterialDialog(requireContext()).show {
                            title(text = "Ok")
                            message(text = "Registro modificado correctamente")
                            icon(R.drawable.baseline_check_circle_24)
                            positiveButton(text = "Aceptar")
                        }

                        // Cerrar el diálogo después de 2 segundos
                        Handler(Looper.getMainLooper()).postDelayed({
                            dialog.dismiss()
                        }, 2000)

                        // Volver al fragmento anterior (PersonaFragment) y finalizar este fragmento
                        findNavController().popBackStack()

                    }

                }
            })

        }

    }


}