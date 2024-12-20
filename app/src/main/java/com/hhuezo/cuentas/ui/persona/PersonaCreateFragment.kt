package com.hhuezo.cuentas.ui.persona

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.hhuezo.cuentas.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class PersonaCreateFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_persona_create, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Nueva persona"

        val nombreEditText = view.findViewById<EditText>(R.id.nombreEditText)
        val documentoEditText = view.findViewById<EditText>(R.id.documentoEditText)
        val telefonoEditText = view.findViewById<EditText>(R.id.telefonoEditText)
        val bancoEditText = view.findViewById<EditText>(R.id.bancoEditText)
        val cuentaEditText = view.findViewById<EditText>(R.id.cuentaEditText)
        val aceptarButton = view.findViewById<Button>(R.id.aceptarButton)

        aceptarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            if (nombre.isEmpty()) {

                MaterialDialog(requireContext()).show {
                    title(text = "Error")
                    message(text = "El campo nombre no puede estar vacío")
                    icon(R.drawable.baseline_error_24)
                    positiveButton(text = "Aceptar")
                }
            } else {
                val json = JSONObject()
                json.put("nombre", nombre)
                json.put("documento", documentoEditText.text.toString())
                json.put("telefono", telefonoEditText.text.toString())
                json.put("banco", bancoEditText.text.toString())
                json.put("cuenta", cuentaEditText.text.toString())

                enviarDatos(json)
            }
        }
    }


    private fun enviarDatos(json: JSONObject) {
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json.toString()
        )
        val request = Request.Builder()
            .url(getString(R.string.url) + "persona")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    requireActivity().runOnUiThread {
                        //Toast.makeText(requireContext(), "Operación exitosa", Toast.LENGTH_SHORT).show()


                        val dialog = MaterialDialog(requireContext()).show {
                            title(text = "Ok")
                            message(text = "Regitro creado correctamente")
                            icon(R.drawable.baseline_check_circle_24)
                            positiveButton(text = "Aceptar")
                        }

                        // Cerrar el diálogo después de 2 segundos
                        Handler(Looper.getMainLooper()).postDelayed({
                            dialog.dismiss()
                        }, 2000)

                        findNavController().popBackStack()

                    }
                } else {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Error: ${response.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error en la solicitud: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

}