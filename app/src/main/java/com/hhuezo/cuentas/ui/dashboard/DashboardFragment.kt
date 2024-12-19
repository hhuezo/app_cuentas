package com.hhuezo.cuentas.ui.dashboard

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.hhuezo.cuentas.R
import com.hhuezo.cuentas.databinding.FragmentDashboardBinding
import com.hhuezo.cuentas.model.DashboardResponse
import com.hhuezo.cuentas.model.HttpClient
import com.hhuezo.cuentas.model.PrestamoAdapter
import com.hhuezo.cuentas.model.PrestamoResponse
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val client by lazy { HttpClient(requireActivity()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Dashboard"
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_opciones, container, false)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /*val loadingProgressBar = view.findViewById<ProgressBar>(R.id.loadingProgressBar)
        loadingProgressBar.visibility = View.VISIBLE*/

        client.get("reportes/1", object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API_ERROR", "Fallo al obtener los datos: ${e.message}")
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error al obtener los datos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                if (responseData != null) {
                    Log.d("API_RESPONSE", responseData)

                    val gson = Gson()
                    val dashboardResponse = gson.fromJson(responseData, DashboardResponse::class.java)

                    val ganancias = dashboardResponse.gananciasPrestamo ?: emptyList()
                    val gananciasMes = dashboardResponse.gananciasPrestamoMes ?: emptyList()

                    Log.d("ganancias", "ganancias " + ganancias.toString())
                    Log.d("gananciasMes", "gananciasMes " + gananciasMes.toString())

                    // Configurar el gráfico de barras
                    val barChart: BarChart = view.findViewById(R.id.barChart)

                    val barEntries = ArrayList<BarEntry>()

                    // Llenar los datos de barras con los valores obtenidos de la API
                    for (i in ganancias.indices) {
                        // Convertir el valor a Float y formatearlo a dos decimales
                        val value = ganancias[i].toFloat() // Usamos los valores directamente
                        barEntries.add(BarEntry(i.toFloat(), value))  // Barra (índice, valor)
                    }

                    val dataSet = BarDataSet(barEntries, "")
                    val barColors = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA)

                    // Si hay más meses, podemos ajustar los colores
                    dataSet.colors = barColors.take(barEntries.size) // Limitamos los colores al tamaño de las barras

                    val barData = BarData(dataSet)
                    barChart.data = barData
                    barChart.setFitBars(true)

                    // Ajustar el tamaño del texto y hacerlo en negrita
                    dataSet.valueTextSize = 10f  // Reducir el tamaño del texto
                    dataSet.setValueTypeface(Typeface.DEFAULT_BOLD)  // Poner el texto en negrita

                    // Formatear los valores sobre las barras a dos decimales y agregar el símbolo "$"
                    dataSet.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            // Formatear los valores con dos decimales y agregar el símbolo "$"
                            return "$" + String.format("%.2f", value)
                        }
                    }

                    val xAxis: XAxis = barChart.xAxis
                    xAxis.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            // Formateamos el valor del eje X con los meses de la respuesta de la API
                            return if (value.toInt() < gananciasMes.size) {
                                gananciasMes[value.toInt()]
                            } else {
                                ""
                            }
                        }
                    }

                    barChart.invalidate() // Refrescar el gráfico

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






    }

}