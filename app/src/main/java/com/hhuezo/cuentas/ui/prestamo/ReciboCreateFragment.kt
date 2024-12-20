package com.hhuezo.cuentas.ui.prestamo

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hhuezo.cuentas.R
import com.hhuezo.cuentas.databinding.FragmentReciboCreateBinding
import com.hhuezo.cuentas.model.HttpClient
import com.hhuezo.cuentas.model.ReciboCreateResponse
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReciboCreateFragment : Fragment() {

    private var _binding: FragmentReciboCreateBinding? = null
    private val binding get() = _binding!!

    private val client by lazy { HttpClient(requireContext()) }

    private lateinit var imageResultLauncher: ActivityResultLauncher<String>
    private var imageUri: Uri? = null
    private var imagenBase64: String? = null

    private var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentReciboCreateBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Nuevo recibo"

        id = arguments?.getInt("id") ?: 0

        val fechaEditText: EditText? = view.findViewById(R.id.fechaEditText)
        val nombreEditText: EditText? = view.findViewById(R.id.nombreEditText)
        val deudaEditText: EditText? = view.findViewById(R.id.deudaEditText)
        val interesEditText: EditText? = view.findViewById(R.id.interesEditText)
        val cantidadEditText: EditText? = view.findViewById(R.id.cantidadEditText)
        val comprobanteImageView: ImageView? = view.findViewById(R.id.comprobanteImageView)

        // Obtener la fecha actual
        val currentDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val todayDate = dateFormat.format(currentDate.time)
        // Establecer la fecha actual en el EditText
        fechaEditText?.setText(todayDate)


        val endpoint = "recibo/$id"
        Log.d("endpoint", endpoint ?: "")
        client.get(endpoint, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error al hacer la petición: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                requireActivity().runOnUiThread {
                    Log.d("Response", responseData ?: "")
                    if (response.isSuccessful) {
                        val gson = Gson()
                        val responseType = object : TypeToken<ReciboCreateResponse>() {}.type
                        val response = gson.fromJson(responseData, ReciboCreateResponse::class.java)
                        val recibo = response.data

                        recibo?.let {
                            nombreEditText?.setText(it.nombre)
                            deudaEditText?.setText(it.remanente)
                            interesEditText?.setText(it.interes)
                            cantidadEditText?.setText(it.total)
                        } ?: run {
                            Toast.makeText(
                                requireContext(), "Error datos nulos", Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            requireContext(), "Error al obtener los datos", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }



        })


        //calendario
        fechaEditText?.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonthOfYear, selectedDayOfMonth ->
                    val formattedDate = String.format(
                        "%02d/%02d/%d",
                        selectedDayOfMonth,
                        selectedMonthOfYear + 1,
                        selectedYear
                    )
                    fechaEditText.setText(formattedDate)
                },
                year,
                month,
                day
            )

            dpd.show()
        }




        imageResultLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    comprobanteImageView?.setImageURI(uri)
                    imageUri = uri
                }
            }

        comprobanteImageView?.setOnClickListener {
            imageResultLauncher.launch("image/*")
        }



        //enviar datos
        val aceptarButton = view.findViewById<Button>(R.id.aceptarButton)
        aceptarButton.setOnClickListener {
            val cantidadText = cantidadEditText?.text.toString()
            val cantidad = cantidadText.toDoubleOrNull()
            if (cantidadText.isEmpty() && cantidad != null && cantidad >= 0) {

                MaterialDialog(requireContext()).show {
                    title(text = "Error")
                    message(text = "El campo nombre no puede estar vacío")
                    icon(R.drawable.baseline_error_24)
                    positiveButton(text = "Aceptar")
                }
            } else {
                val json = JSONObject()
                json.put("prestamo_id", id)
                json.put("fecha", fechaEditText?.text.toString())
                json.put("cantidad", cantidadEditText?.text.toString())
                json.put("interes", interesEditText?.text.toString())

                Log.d("body :",json.toString())
                if (imageUri != null) {
                    try {
                        requireContext().contentResolver.openInputStream(imageUri!!).use { inputStream ->
                            val bytes = inputStream?.readBytes()
                            imagenBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                            //Log.d("ImagenBase64", imagenBase64 ?: "La cadena es nula")
                        }
                    } catch (e: IOException) {
                        Log.e("ImagenBase64", "Error al leer la imagen", e)
                    }
                }
                json.put("comprobante", if (imagenBase64.isNullOrEmpty()) JSONObject.NULL else imagenBase64)
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
            .url(getString(R.string.url) + "recibo")
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