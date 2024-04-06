package com.example.rendimientoplanta.presentacion.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rendimientoplanta.base.model.CardMenu
import com.example.rendimientoplanta.base.model.ChartPie
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.data.repository.ProductoRepo
import com.example.rendimientoplanta.domain.impldomain.ProductoCase
import com.example.rendimientoplanta.presentacion.adapter.CardMenuAdapter
import com.example.rendimientoplanta.presentacion.adapter.CardMenuRowAdapter
import com.example.rendimientoplanta.presentacion.factory.ProductoFactory
import com.example.rendimientoplanta.presentacion.viewmodel.ProductoVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_producto.*

class ProductoFragment : BaseFragment(true, true, "Productos"),
    CardMenuRowAdapter.onCardMenuClickListener {
    private val TAG = "ProductoFragment"
    private val earning = intArrayOf(20000, 30000)
    private val months = arrayOf("Completado", "Pendiente")
    override fun getViewID(): Int = R.layout.fragment_producto

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ProductoFactory(ProductoCase(ProductoRepo()))
        ).get(ProductoVM::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView_img.layoutManager = LinearLayoutManager(requireContext())
        recyclerView_pie.layoutManager = LinearLayoutManager(requireContext())
        setupRecyclerView()
        setupPieChart("Tallos pendientes", "Tallos procesados", 0, 0)
        getStems()
    }

    private fun getStems() {
        viewModel.getStems(user).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sLoading} Asignando tallos")
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sSuccess} Tallos asignados: ${result.data}")
                    setupPieChart("Tallos pendientes", "Tallos procesados", result.data[0], result.data[1])
                }
                is Resource.Failure -> {
                    if(result.exception.message.toString()!="null")
                        findNavController().navigate(ProductoFragmentDirections
                            .actionProductoFragmentToMessageBottomSheet("Error al obtener los tallos para el reporte de pastel.\nMotivo_: ${result.exception.message}"))
                    Log.w(TAG,"${MessageBuilder.sFailure} Respuesta excepcion: ${result.exception.message}")
                    showProgressBar(false, progress_horizontal)

                }
            }
        })
    }

    private fun setupPieChart(green: String, orange: String, igreen: Int, iorange: Int) {
        val list = listOf(
            ChartPie(arrayOf(green, orange), intArrayOf(igreen, iorange))
        )
        recyclerView_pie.adapter = CardMenuAdapter(requireContext(), list)
    }

    private fun setupRecyclerView() {
        val list = listOf(
            CardMenu(R.drawable.card_menu_asignar, 1),
            CardMenu(R.drawable.card_menu_desasignar, 2)
        )
        recyclerView_img.adapter = CardMenuRowAdapter(requireContext(), list, this)
    }

    override fun onItemClick(id: Int) {
        when (id) {
            1 -> {
                Log.w(TAG, "Asignar")
                findNavController().navigate(R.id.action_productoFragment_to_asignarProductoFragment)
            }
            2 -> {
                Log.w(TAG, "Desasignar")
                findNavController().navigate(R.id.action_productoFragment_to_desasignarProductoFragment)
            }
        }
    }
}