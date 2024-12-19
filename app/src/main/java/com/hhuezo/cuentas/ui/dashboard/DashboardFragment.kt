package com.hhuezo.cuentas.ui.dashboard

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.hhuezo.cuentas.R
import com.hhuezo.cuentas.databinding.FragmentDashboardBinding


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

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

        // Configurar el gráfico de barras
        val barChart: BarChart = view.findViewById(R.id.barChart)

        val barEntries = ArrayList<BarEntry>()
        barEntries.add(BarEntry(0f, 35f))  // Barra 1 (Jul)
        barEntries.add(BarEntry(1f, 800f))  // Barra 2 (Ago)
        barEntries.add(BarEntry(2f, 900f))  // Barra 3 (Sep)
        barEntries.add(BarEntry(3f, 1000f))  // Barra 4 (Oct)
        barEntries.add(BarEntry(4f, 1100f))  // Barra 5 (Nov))
        barEntries.add(BarEntry(5f, 1200f))  // Barra 6 (Dic)

        val dataSet = BarDataSet(barEntries, "Prestamos")
        val barColors = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA)
        dataSet.colors = barColors
        val barData = BarData(dataSet)

        barChart.data = barData
        barChart.setFitBars(true)
        dataSet.valueTextSize = 12f

        val xAxis: XAxis = barChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when (value) {
                    0f -> "Jul"
                    1f -> "Ago"
                    2f -> "Sep"
                    3f -> "Oct"
                    4f -> "Nov"
                    5f -> "Dic"
                    else -> ""
                }
            }
        }

        barChart.invalidate()

    }

}