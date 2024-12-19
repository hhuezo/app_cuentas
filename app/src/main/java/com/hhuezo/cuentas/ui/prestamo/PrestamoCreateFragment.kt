package com.hhuezo.cuentas.ui.prestamo

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hhuezo.cuentas.R
import com.hhuezo.cuentas.databinding.FragmentPrestamoCreateBinding
import com.hhuezo.cuentas.model.HttpClient
import com.hhuezo.cuentas.model.PrestamoCreateResponse
import com.hhuezo.cuentas.model.Solicitante
import com.hhuezo.cuentas.model.TipoPago
import com.hhuezo.cuentas.model.Usuario
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.afollestad.materialdialogs.MaterialDialog

class PrestamoCreateFragment : Fragment() {


    private val client by lazy { HttpClient(requireActivity()) }
    private lateinit var personas: List<Solicitante>
    private lateinit var tiposPago: List<TipoPago>
    private lateinit var usuarios: List<Usuario>
    private var selectedPersonId: Int? = null
    private var selectedTipoPagoId: Int? = null
    private var selectedAdministradorId: Int? = null
    private var imageUri: Uri? = null
    private var imagenBase64: String? = null

    private lateinit var imageResultLauncher: ActivityResultLauncher<String>


    private var _binding: FragmentPrestamoCreateBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
// Inflate the layout for this fragment
        _binding = FragmentPrestamoCreateBinding.inflate(inflater, container, false)

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Nuevo préstamo"


        val personaAutoComplete: AutoCompleteTextView? = view.findViewById(R.id.personaAutoComplete)
        val tipoPagoSpinner: Spinner? = view.findViewById(R.id.tipoPagoSpinner)
        val administradorSpinner: Spinner? = view.findViewById(R.id.administradorSpinner)

        val comprobanteImageView: ImageView? = view.findViewById(R.id.comprobanteImageView)
        val cantidadEditText: EditText? = view.findViewById(R.id.cantidadEditText)
        val interesEditText: EditText? = view.findViewById(R.id.interesEditText)
        val numeroPagosEditText: EditText? = view.findViewById(R.id.numeroPagosEditText)
        val amortizacionSwitch: Switch? = view.findViewById(R.id.amortizacionSwitch)
        val pagoEspecificoEditText: EditText? = view.findViewById(R.id.pagoEspecificoEditText)
        val observacionEditText: EditText? = view.findViewById(R.id.observacionEditText)
        val aceptarButton: Button? = view.findViewById(R.id.aceptarButton)

        val fechaEditText: EditText? = view.findViewById(R.id.fechaEditText)

        // Obtener la fecha actual
        val currentDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val todayDate = dateFormat.format(currentDate.time)
        // Establecer la fecha actual en el EditText
        fechaEditText?.setText(todayDate)


        val endpoint = "prestamo/create"
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
                    // Log.d("Response", responseData ?: "")
                    if (response.isSuccessful) {
                        val gson = Gson()
                        val responseType = object : TypeToken<PrestamoCreateResponse>() {}.type
                        val response =
                            gson.fromJson(responseData, PrestamoCreateResponse::class.java)

                        // Llenar el AutoCompleteTextView de personas
                        personas = response.data.personas
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            personas.map { it.nombre })
                        personaAutoComplete?.setAdapter(adapter)

                        Log.d("Response", response.data.tipos_pago.toString() ?: "hola")

                        // Llenar el Spinner de tipos_pago
                        tiposPago = response.data.tipos_pago
                        val adapterTiposPago = ArrayAdapter(requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            tiposPago.map { it.nombre })
                        tipoPagoSpinner?.adapter = adapterTiposPago

                        // Inicializar selectedTipoPagoId con el ID del primer tipo de pago
                        if (tiposPago.isNotEmpty()) {
                            selectedTipoPagoId = tiposPago[0].id
                        }

                        // Llenar el Spinner de usuarios
                        usuarios = response.data.usuarios
                        val adapterUsuarios = ArrayAdapter(requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            usuarios.map { it.username })
                        administradorSpinner?.adapter = adapterUsuarios

                        // Inicializar selectedAdministradorId con el ID del primer usuario
                        if (usuarios.isNotEmpty()) {
                            selectedAdministradorId = usuarios[0].id
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error al obtener los datos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })



        personaAutoComplete?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedPersonName = parent.adapter.getItem(position) as String
                val selectedPerson = personas.find { it.nombre == selectedPersonName }
                selectedPersonId = selectedPerson?.id
                Log.d(
                    "SelectedPerson",
                    "posicion $position ID: ${selectedPerson?.id}, Nombre: ${selectedPerson?.nombre}"
                )
            }


        personaAutoComplete?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                selectedPersonId = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        tipoPagoSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedTipoPagoId = tiposPago[position].id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        administradorSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedAdministradorId = usuarios[position].id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }





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


        //boton aceptar

        aceptarButton?.setOnClickListener {

            val personaId = selectedPersonId.toString()
            val cantidad = cantidadEditText?.text.toString().toDoubleOrNull() ?: 0.0
            val interes = interesEditText?.text.toString().toIntOrNull() ?: 0
            val numero_pagos = numeroPagosEditText?.text.toString().toIntOrNull() ?: 0
            val tipoPagoId = tiposPago[tipoPagoSpinner?.selectedItemPosition ?: 0].id
            val fecha = fechaEditText?.text.toString()
            val amortizacion = amortizacionSwitch?.isChecked ?: false
            val administrador = usuarios[administradorSpinner?.selectedItemPosition ?: 0].id
            val pagoEspecifico = pagoEspecificoEditText?.text.toString()
            val observacion = observacionEditText?.text.toString()

            if (imageUri != null) {
                try {
                    requireContext().contentResolver.openInputStream(imageUri!!)
                        .use { inputStream ->
                            val bytes = inputStream?.readBytes()
                            imagenBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                            //Log.d("ImagenBase64", imagenBase64 ?: "La cadena es nula")
                        }
                } catch (e: IOException) {
                    Log.e("ImagenBase64", "Error al leer la imagen", e)
                }
            }


            if (selectedPersonId == null || cantidad <= 0 || numero_pagos <= 0) {
                val dialog = MaterialDialog(requireContext()).show {
                    title(text = "Error")
                    message(text = "Por favor, complete todos los campos correctamente.")
                    icon(R.drawable.baseline_error_24)
                    positiveButton(text = "Aceptar")
                }

                // Cerrar el diálogo después de 2 segundos
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                }, 2000)
                return@setOnClickListener
            }

            // Crear el objeto JSON
            val json = JSONObject().apply {
                put("persona_id", personaId)
                put("cantidad", cantidad)
                put("interes", interes)
                put("tipo_pago_id", tipoPagoId)
                put("fecha", fecha)
                put("amortizacion", amortizacion)
                put(
                    "comprobante",
                    if (imagenBase64.isNullOrEmpty()) JSONObject.NULL else imagenBase64
                )
                put("administrador", administrador)
                put("pago_especifico", pagoEspecifico)
                put("observacion", observacion)
                put("numero_pagos", numero_pagos)
            }.toString()

            // Realizar la petición POST
            val endpoint = "prestamo"
            client.post(endpoint, json, object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                    val dialog = MaterialDialog(requireContext()).show {
                        title(text = "Error")
                        message(text = "Error al hacer la petición: ${e.message}")
                        icon(R.drawable.baseline_error_24)
                        positiveButton(text = "Aceptar")
                    }

                    // Cerrar el diálogo después de 2 segundos
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialog.dismiss()
                    }, 2000)

                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    Log.d("Response", responseData ?: "No se recibió ninguna respuesta")

                    requireActivity().runOnUiThread {
                        if (response.isSuccessful) {
                            val dialog = MaterialDialog(requireContext()).show {
                                title(text = "Ok")
                                message(text = "Registro guardado correctamente")
                                icon(R.drawable.baseline_check_circle_24)
                                positiveButton(text = "Aceptar")
                            }

                            // Cerrar el diálogo después de 2 segundos
                            Handler(Looper.getMainLooper()).postDelayed({
                                dialog.dismiss()
                            }, 2000)

                            requireActivity().supportFragmentManager.popBackStack()
                        } else {
                            val dialog = MaterialDialog(requireContext()).show {
                                title(text = "Error")
                                message(text = "Error al enviar los datos")
                                icon(R.drawable.baseline_error_24)
                                positiveButton(text = "Aceptar")
                            }

                            // Cerrar el diálogo después de 2 segundos
                            Handler(Looper.getMainLooper()).postDelayed({
                                dialog.dismiss()
                            }, 2000)
                        }
                    }
                }
            })
        }


    }

}