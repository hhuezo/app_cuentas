package com.hhuezo.cuentas.ui.prestamo

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.hhuezo.cuentas.R
import com.hhuezo.cuentas.databinding.FragmentPrestamoShowBinding
import com.hhuezo.cuentas.model.HttpClient
import com.hhuezo.cuentas.model.PrestamoShowResponse
import com.hhuezo.cuentas.model.ReciboAdapter
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class PrestamoShowFragment : Fragment() {

    private var _binding: FragmentPrestamoShowBinding? = null
    private val binding get() = _binding!!

    private var estado_id: Int = 0
    private var id = 0
    private var rolId = "1"
    private val client by lazy { HttpClient(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPrestamoShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Préstamo"

        //opteniendo el id del fragment anterior
        id = arguments?.getInt("id") ?: 0

        val URL = getString(R.string.url)


        val recibosRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        val layoutManager = LinearLayoutManager(context)
        recibosRecyclerView.layoutManager = layoutManager

        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.loadingProgressBar)
        loadingProgressBar.visibility = View.VISIBLE // Mostrar el ProgressBar

        // Después de mostrar los datos del préstamo
        val formularioLayout = view.findViewById<LinearLayout>(R.id.formularioLayout)



        val cantidadTextView = view.findViewById<TextView>(R.id.cantidadTextView)
        val codigoTextView = view.findViewById<TextView>(R.id.codigoTextView)
        val nombretextView = view.findViewById<TextView>(R.id.nombretextView)
        val interesTextView = view.findViewById<TextView>(R.id.interesTextView)
        val fechatextView = view.findViewById<TextView>(R.id.fechatextView)
        val tipotextView = view.findViewById<TextView>(R.id.tipotextView)
        val remanenteTextView = view.findViewById<TextView>(R.id.remanenteTextView)







        val fab: FloatingActionButton = view.findViewById(R.id.fab)

        if (rolId != "1") {
            fab.visibility = View.GONE
        }

        binding.fab.setOnClickListener {
            // Navegar al PrestamoCreateFragment
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_opciones)

            val window = dialog.window
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )


            val btnRecibo = dialog.findViewById<Button>(R.id.btnRecibo)
            val btnCargo = dialog.findViewById<Button>(R.id.btnCargo)
            val btnCancelar = dialog.findViewById<Button>(R.id.btnCancelar)


            btnRecibo.setOnClickListener {
                /*dialog.dismiss()
                val action = PrestamoShowFragmentDirections.actionPrestamoShowFragmentToReciboCreateFragment(id)
                findNavController().navigate(action)*/

            }

            btnCargo.setOnClickListener {
               /* dialog.dismiss()
                val action = PrestamoShowFragmentDirections.actionPrestamoShowFragmentToCargoCreateFragment(id)
                findNavController().navigate(action)*/

            }


            btnCancelar.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }






        val client = OkHttpClient()
        Log.d("TAG", getString(R.string.url) + "prestamo/$id")

        client.newCall(
            Request.Builder()
                .url(getString(R.string.url) + "prestamo/$id")
                .build()
        )
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("API_ERROR", "Fallo al obtener los datos: ${e.message}")
                    loadingProgressBar.visibility =
                        View.GONE // Ocultar el ProgressBar en caso de error
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()

                    // Imprime la respuesta en la consola de registro
                    if (responseData != null) {
                        Log.d("API_RESPONSE", responseData)
                    }

                    // Utiliza Gson para convertir la respuesta JSON en una lista de personas
                    val gson = Gson()
                    val prestamoEditResponse =
                        gson.fromJson(responseData, PrestamoShowResponse::class.java)
                    val prestamo = prestamoEditResponse.data.prestamo


                    if (prestamo != null) {
                        Log.d("API_RESPONSE prestamo", prestamo.toString())
                    }

                    estado_id = prestamo.estado

                    // Actualiza los controles en el hilo principal
                    requireActivity().runOnUiThread {
                        cantidadTextView.text = "$" + prestamo.cantidad
                        codigoTextView.text = prestamo.codigo
                        nombretextView.text = prestamo.persona
                        if (prestamo.pago_especifico != null) {
                            interesTextView.text =
                                "Cuota fija: $" + prestamo.pago_especifico.toString()
                        } else {
                            interesTextView.text = "Interes: " + prestamo.interes.toString() + "%"
                        }

                        fechatextView.text = prestamo.fecha
                        tipotextView.text = "Tipo pago: " + prestamo.tipo
                        remanenteTextView.text = "Deuda: $" + prestamo.remanente

                        formularioLayout.visibility = View.VISIBLE
                    }

                    val recibos = prestamoEditResponse?.data?.recibos ?: emptyList()
                    if (recibos != null) {
                        Log.d("API_RESPONSE recibos", recibos.toString())

                    }
                    requireActivity().runOnUiThread {
                        recibosRecyclerView.adapter =
                            ReciboAdapter(recibos, object : ReciboAdapter.OnReciboClickListener {
                                override fun onReciboClick(id: Int) {
                                    // Aquí manejas el clic en un recibo
                                }
                            })
                    }



                    requireActivity().runOnUiThread {
                        loadingProgressBar.visibility = View.GONE
                    }


                }
            })

    }


}