package com.example.rendimientoplanta.presentacion.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.model.ChartVertical
import com.example.rendimientoplanta.base.pojos.Linea
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.utils.validators.CalcuTimeValidators
import com.example.rendimientoplanta.data.repository.ReporteRepo
import com.example.rendimientoplanta.domain.impldomain.ReporteCase
import com.example.rendimientoplanta.presentacion.adapter.CardMenuAdapter
import com.example.rendimientoplanta.presentacion.factory.ReporteFactory
import com.example.rendimientoplanta.presentacion.viewmodel.ReporteVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_reporte.*
import kotlin.collections.ArrayList


class ReporteFragment : BaseFragment(true, true, "Reporte") {
    private val TAG = "ReporteFragment"

    private var operariosEnLinea = ArrayList<OperarioLinea>()
    private var misLineas = ArrayList<Linea>()
    private var lineaSeleccionada = 0
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ReporteFactory(ReporteCase(ReporteRepo()))
        ).get(ReporteVM::class.java)
    }
    override fun getViewID(): Int = R.layout.fragment_reporte

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView_vertical_lineal.layoutManager = LinearLayoutManager(requireContext())
        recyclerView_vertical_operario.layoutManager = LinearLayoutManager(requireContext())
        setupVerticalLinealChart(
            ChartVertical("Reporte por línea", CalcuTimeValidators.getArrayDays(),
                CalcuTimeValidators.getArrayTallos(), CalcuTimeValidators.getArrayPorcentajes()))
        getLineasPorFinca()
        setupVerticalOperarioChart(ChartVertical("Reporte por operario", CalcuTimeValidators.getArrayDays(),
            CalcuTimeValidators.getArrayTallos(), CalcuTimeValidators.getArrayPorcentajes()))
    }

    fun getLineasPorFinca(){
        viewModel.getLineas(user.fincaId).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.w(TAG, "${MessageBuilder.sLoading} Obteniendo líneas")
                    showProgressBar(true, progress_horizontal)
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    misLineas = result.data
                    initSpinnerLinea()
                }
                is Resource.Failure -> {
                    findNavController().navigate(ReporteFragmentDirections
                        .actionReporteFragmentToMessageBottomSheet
                            ("Error al obtener las líneas. \nMotivo_: ${result.exception.message}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    private fun initSpinnerLinea() {
        val array = ArrayList<String>()
        misLineas.forEach { array.add(it.nombre) }

        array.sortWith { o1, o2 -> Integer.parseInt(o1.substring(6)).compareTo(Integer.parseInt(o2.substring(6)))}


        spinnerLinea.adapter = ArrayAdapter(
            requireContext(),
            R.layout.custom_spinner_list_item, array
        )
        spinnerLineasOnItemSelectedListener()
    }

    fun spinnerLineasOnItemSelectedListener(){
        spinnerLinea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                lineaSeleccionada = parent!!.getItemAtPosition(position).toString().substring(6).toInt()
                setupVerticalOperarioChart(ChartVertical("Reporte por operario", CalcuTimeValidators.getArrayDays(),
                    CalcuTimeValidators.getArrayTallos(), CalcuTimeValidators.getArrayPorcentajes()))
                getReportePorLinea()

            }
        }
    }

    fun getOperarios() {
        viewModel.getOperarios(user.fincaId, lineaSeleccionada).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.w(TAG, "${MessageBuilder.sLoading} Obteniendo operarios")
                    showProgressBar(true, progress_horizontal)
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    operariosEnLinea = result.data
                    initSpinnerOperario()
                }
                is Resource.Failure -> {
                    findNavController().navigate(ReporteFragmentDirections
                        .actionReporteFragmentToMessageBottomSheet
                            ("Error al obtener los operarios. \nMotivo_: ${result.exception.message}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    private fun initSpinnerOperario() {
        val array = ArrayList<String>()
        operariosEnLinea.forEach { array.add("${it.nombre} ${it.apellidos}") }
        array.sortWith { o1, o2 -> o1.compareTo(o2)}

        spinnerOperario.adapter = ArrayAdapter(
            requireContext(),
            R.layout.custom_spinner_list_item, array
        )
        spinnerOperarioOnItemSelectedListener()
    }


    fun spinnerOperarioOnItemSelectedListener(){
        spinnerOperario.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var operarioId = 0
                operariosEnLinea.forEach { if(parent!!.getItemAtPosition(position).toString() == "${it.nombre} ${it.apellidos}") operarioId = it.operarioId }
                getReportePorOperario(lineaSeleccionada, operarioId)
            }
        }
    }

    private fun getReportePorLinea() {
        viewModel.getReportePorLinea(user.fincaId, lineaSeleccionada).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.w(TAG, "${MessageBuilder.sLoading} Obteniendo reporte por linea")
                    showProgressBar(true, progress_horizontal)
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    getOperarios()
                    setupVerticalLinealChart(result.data)
                }
                is Resource.Failure -> {
                    findNavController().navigate(ReporteFragmentDirections
                        .actionReporteFragmentToMessageBottomSheet
                            ("Error al obtener el reporte por línea. \nMotivo_: ${result.exception.message}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    private fun getReportePorOperario(lineaId: Int, operarioId: Int) {
        viewModel.getReportePorOperario(user.fincaId, lineaId, operarioId).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.w(TAG, "${MessageBuilder.sLoading} Obteniendo reporte por operario")
                    showProgressBar(true, progress_horizontal)
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    setupVerticalOperarioChart(result.data)
                }
                is Resource.Failure -> {
                    findNavController().navigate(ReporteFragmentDirections
                        .actionReporteFragmentToMessageBottomSheet
                            ("Error al obtener el reporte por operario. \nMotivo_: ${result.exception.message}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    private fun setupVerticalLinealChart(data: ChartVertical) {
        recyclerView_vertical_lineal.adapter = CardMenuAdapter(requireContext(), listOf(data))
    }

    private fun setupVerticalOperarioChart(data: ChartVertical) {
        recyclerView_vertical_operario.adapter = CardMenuAdapter(requireContext(), listOf(data))
    }
}