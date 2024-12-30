package com.hhuezo.cuentas.ui.home

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hhuezo.cuentas.R
import com.hhuezo.cuentas.databinding.FragmentHomeBinding
import com.hhuezo.cuentas.model.HttpClient
import com.hhuezo.cuentas.model.PagoMensualAdapter
import com.hhuezo.cuentas.model.PrestamoAdapter
import com.hhuezo.cuentas.model.ResponsePagoData
import com.hhuezo.cuentas.ui.prestamo.PrestamoFragmentDirections
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment(), PagoMensualAdapter.OnPagoMensualClickListener {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private val client by lazy { HttpClient(requireContext()) }

    var userId: String? = null
    var rolId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Acceder a SharedPreferences y obtener los valores
        val sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", "defaultUserId")
        rolId = sharedPreferences.getString("rolId", "defaultRolId")

        Log.d("preferecias","User ID: $userId, Rol ID: $rolId")

        // Opcional: Imprimir los valores para verificar
        println("User ID: $userId, Rol ID: $rolId")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val loadingProgressBar: ProgressBar = binding.loadingProgressBar
        loadingProgressBar.visibility = View.VISIBLE

        val fechasText: TextView? = binding.fechasText

        // Obtener la fecha actual
        val currentDate = LocalDate.now()

        // Obtener el primer día del mes actual
        val primerDiaDelMes = currentDate.with(TemporalAdjusters.firstDayOfMonth())

        // Formatear la fecha en el formato deseado (por ejemplo, "dd/MM/yyyy")
        val primerDiaFormateado = primerDiaDelMes.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        // Obtener el último día del mes actual
        val ultimoDiaDelMes = currentDate.with(TemporalAdjusters.lastDayOfMonth())

        // Formatear la fecha en el formato deseado
        val ultimoDiaFormateado = ultimoDiaDelMes.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))


        fechasText?.setText(primerDiaFormateado + " - " + ultimoDiaFormateado)



        val pagosRecyclerView: RecyclerView? = binding.recyclerView
        pagosRecyclerView?.layoutManager = LinearLayoutManager(requireContext())


        // Crear el adaptador con una lista vacía inicialmente
        val adapter =  PagoMensualAdapter(emptyList(), object : PagoMensualAdapter.OnPagoMensualClickListener {
            override fun onReportePagoClick(id: Int) {
                // Manejar el clic en un elemento del RecyclerView si es necesario
            }
        })

        pagosRecyclerView?.adapter = adapter






        client.get("reportes?usuario_id=1&rol=1", object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API_ERROR", "Fallo al obtener los datos: ${e.message}")
                loadingProgressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                if (responseData != null) {
                    Log.d("API_RESPONSE", responseData)

                    val gson = Gson()
                    val pagosResponse = gson.fromJson(responseData, ResponsePagoData::class.java)
                    val pagos = pagosResponse?.data?.pagos ?: emptyList()

                    requireActivity().runOnUiThread {
                        pagosRecyclerView?.adapter = PagoMensualAdapter(pagos,this@HomeFragment)
                        loadingProgressBar.visibility = View.GONE
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


        val cardViewFechas: CardView? = binding.cardViewFechas
        cardViewFechas?.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_date_picker)

            val window = dialog.window
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )


            val etFechaInicio = dialog.findViewById<EditText>(R.id.etFechaInicio)
            val etFechaFin = dialog.findViewById<EditText>(R.id.etFechaFin)
            val btnAceptar = dialog.findViewById<Button>(R.id.loginButton)
            val btnCancelar = dialog.findViewById<Button>(R.id.btnCancelar)

            etFechaInicio?.let { editText ->
                if (editText.text.isEmpty()) {
                    editText.setText(primerDiaFormateado)
                }
            }
            etFechaFin?.let { editText ->
                if (editText.text.isEmpty()) {
                    editText.setText(ultimoDiaFormateado)
                }
            }



            etFechaInicio.setOnClickListener {
                showDatePickerDialog(etFechaInicio)
            }

            etFechaFin.setOnClickListener {
                showDatePickerDialog(etFechaFin)
            }




            btnAceptar.setOnClickListener {
                loadingProgressBar.visibility =
                    View.VISIBLE // Mostrar el ProgressBar al iniciar la consulta

                val fechaInicio = etFechaInicio.text.toString()
                val fechaFin = etFechaFin.text.toString()

                val dateFormatInput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val dateFormatOutput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val fechaInicioFormateada =
                    dateFormatOutput.format(dateFormatInput.parse(fechaInicio))
                val fechaFinFormateada = dateFormatOutput.format(dateFormatInput.parse(fechaFin))

                //Log.d("FECHAS", "Fecha inicio: $fechaInicio, Fecha fin: $fechaFin")

                fechasText?.text = "$fechaInicio - $fechaFin"
                try {
                    val dateInicio = dateFormatInput.parse(fechaInicio)
                    val dateFin = dateFormatInput.parse(fechaFin)

                    val fechaInicioFormateada = dateFormatOutput.format(dateInicio)
                    val fechaFinFormateada = dateFormatOutput.format(dateFin)

                    Log.d(
                        "FECHAS",
                        "Fecha de inicio: $fechaInicioFormateada, Fecha fin: $fechaFinFormateada"
                    )

                    dialog.dismiss()

                    // Hacer la consulta a la API
                    val url = "reportes?fecha_inicio=$fechaInicio&fecha_final=$fechaFin&usuario_id=$userId&rol=$rolId"

                    Log.e("url_home", url)
                    client.get(url, object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.e("API_ERROR", "Fallo al obtener los datos: ${e.message}")
                            loadingProgressBar.visibility =
                                View.GONE // Ocultar el ProgressBar en caso de error
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val responseData = response.body?.string()

                            if (responseData != null) {
                                //Log.d("API_RESPONSE", responseData)

                                val gson = Gson()
                                val pagosResponse =
                                    gson.fromJson(responseData, ResponsePagoData::class.java)
                                val pagos = pagosResponse?.data?.pagos ?: emptyList()

                                requireActivity().runOnUiThread {
                                    pagosRecyclerView?.adapter = PagoMensualAdapter(
                                        pagos,
                                        object : PagoMensualAdapter.OnPagoMensualClickListener {
                                            override fun onReportePagoClick(id: Int) {
                                                // Aquí puedes manejar el clic en un elemento del RecyclerView si es necesario
                                                Log.e("API_ERROR", "este es el id: ${id}")
                                                requireActivity().runOnUiThread {
                                                    Toast.makeText(
                                                        requireContext(),
                                                        "Este es el id : ${id}",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        })
                                    loadingProgressBar.visibility =
                                        View.GONE // Ocultar el ProgressBar al finalizar la consulta
                                }
                            } else {
                                requireActivity().runOnUiThread {
                                    Toast.makeText(
                                        requireContext(),
                                        "No hay datos: ${response.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadingProgressBar.visibility =
                                        View.GONE // Ocultar el ProgressBar en caso de error
                                }
                            }
                        }
                    })

                } catch (e: ParseException) {
                    Log.e("FECHAS", "Error al parsear las fechas: ${e.message}")
                    loadingProgressBar.visibility =
                        View.GONE // Ocultar el ProgressBar en caso de error
                }
            }



            btnCancelar.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }





        return root
    }

    private fun showDatePickerDialog(editText: EditText) {
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                editText.setText(dateFormat.format(cal.time))
            }

        DatePickerDialog(
            requireContext(),
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onReportePagoClick(id: Int) {
        //Log.d("OnClickListener", "Elemento con ID: $id clickeado")
        val action = HomeFragmentDirections.actionNavigationHomeToReciboRegistrarFragment(id)
        findNavController().navigate(action)
    }
}