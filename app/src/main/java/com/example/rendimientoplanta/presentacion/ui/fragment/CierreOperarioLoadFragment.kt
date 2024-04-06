package com.example.rendimientoplanta.presentacion.ui.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.pojos.CierreOperarioLoad
import com.example.rendimientoplanta.data.repository.CierreOperarioRepo
import com.example.rendimientoplanta.domain.impldomain.CierreOperarioCase
import com.example.rendimientoplanta.presentacion.adapter.CardMenuCierreOperarioAdapter
import com.example.rendimientoplanta.presentacion.factory.CierreOperarioFactory
import com.example.rendimientoplanta.presentacion.viewmodel.CierreOperarioVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_asignar_producto.*
import kotlinx.android.synthetic.main.fragment_cierre_linea.recyclerView


class CierreOperarioLoadFragment : BaseFragment(true, true, "Lista de cierres de operarios"){
    private val TAG = "CierreOperarioLoadFragment"
    private val args: CierreOperarioLoadFragmentArgs by navArgs()

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            CierreOperarioFactory(CierreOperarioCase(CierreOperarioRepo()))
        ).get(CierreOperarioVM::class.java)
    }

    override fun getViewID():Int = R.layout.fragment_cierre_operario_load

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        getCierresOperario()
    }

    private fun setupRecyclerView(data: ArrayList<CierreOperarioLoad>){
        recyclerView.adapter = CardMenuCierreOperarioAdapter(requireContext(), data)
    }

    private fun getCierresOperario(){
        viewModel.getCierresOperarios(args.cierreLinea!!).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Obteniendo cierres de operarios...")
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "Cierres de operarios obtenidos ${result.data}")
                    setupRecyclerView(result.data)
                }
                is Resource.Failure -> {
                    findNavController().navigate(CierreOperarioLoadFragmentDirections
                        .actionCierreOperarioLoadFragmentToMessageBottomSheet
                            ("No se puedieron obtener los cierres de operarios. \nMotivo_: ${result.exception.message}"))
                    Log.w(TAG, "${MessageBuilder.sFailure}, no se pudieron obtener las lineas")
                }
            }
        })
    }

}