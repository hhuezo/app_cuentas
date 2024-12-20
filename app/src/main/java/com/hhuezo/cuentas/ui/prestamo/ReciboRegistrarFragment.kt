package com.hhuezo.cuentas.ui.prestamo

import android.graphics.BitmapFactory
import android.net.Uri
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
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hhuezo.cuentas.R
import com.hhuezo.cuentas.databinding.FragmentReciboRegistrarBinding
import com.hhuezo.cuentas.model.HttpClient
import com.hhuezo.cuentas.model.ReciboCreateResponse
import com.hhuezo.cuentas.model.ReciboRegistroResponse
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class ReciboRegistrarFragment : Fragment() {

    private var _binding: FragmentReciboRegistrarBinding? = null
    private val binding get() = _binding!!

    private var estado_id: Int = 0
    private var id = 0
    private var rolId = "1"
    private val client by lazy { HttpClient(requireContext()) }

    private lateinit var imageResultLauncher: ActivityResultLauncher<String>
    private var imageUri: Uri? = null
    private var imagenBase64: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recibo_registrar, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Recibo"


        id = arguments?.getInt("id") ?: 0

        val fechaEditText: EditText? = view.findViewById(R.id.fechaEditText)
        val nombreEditText: EditText? = view.findViewById(R.id.nombreEditText)
        val cantidadEditText: EditText? = view.findViewById(R.id.cantidadEditText)



        val totalEditText: EditText? = view.findViewById(R.id.totalEditText)
        val interesEditText: EditText? = view.findViewById(R.id.interesEditText)

        val comprobanteImageView: ImageView? = view.findViewById(R.id.comprobanteImageView)

        val estadoSwitch: Switch? = view.findViewById(R.id.estadoSwitch)

        val aceptarButton: Button? = view.findViewById(R.id.aceptarButton)

        val endpoint = "recibo/$id/edit"
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
                        val responseType = object : TypeToken<ReciboRegistroResponse>() {}.type
                        val response = gson.fromJson(responseData, ReciboRegistroResponse::class.java)
                        val recibo = response.data

                        recibo?.let {
                            fechaEditText?.setText(it.fecha)
                            nombreEditText?.setText(it.nombre)
                            cantidadEditText?.setText(it.cantidad)
                            interesEditText?.setText(it.interes)
                            totalEditText?.setText(it.total)

                            estadoSwitch?.isChecked = it.estado == 2

                            if (!it.comprobante.isNullOrEmpty()) {
                                val base64String = if (it.comprobante.startsWith("data:image")) {
                                    it.comprobante.substringAfter("base64,")
                                } else {
                                    it.comprobante
                                }

                                try {
                                    // Decodificar Base64
                                    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
                                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                                    // Establecer la imagen en el ImageView
                                    comprobanteImageView?.setImageBitmap(bitmap)
                                } catch (e: IllegalArgumentException) {
                                    e.printStackTrace()
                                    // Manejar errores si el Base64 no es válido
                                }

                            }


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



        aceptarButton?.setOnClickListener {

            val estado = estadoSwitch?.isChecked == true  // true si el switch está activado

            val json = JSONObject()

            json.put("estado", estado)
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

            //Log.d("datos",  "datos "+json)
            enviarDatos(json)

        }






    }

    private fun enviarDatos(json: JSONObject) {
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json.toString()
        )


        val request = Request.Builder()
            .url(getString(R.string.url) + "recibo/$id")
            .put(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {

                    val responseBody = response.body?.string()

                    // Imprimir el cuerpo completo de la respuesta en el log
                    Log.d("datos", "response body: $responseBody")


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