package com.example.rendimientoplanta.presentacion.ui.bottomsheet

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.pojos.Motivo
import com.example.rendimientoplanta.data.repository.RecesoRepo
import com.example.rendimientoplanta.domain.impldomain.RecesoCase
import com.example.rendimientoplanta.presentacion.adapter.AdapterMotivo
import com.example.rendimientoplanta.presentacion.factory.RecesoFactory
import com.example.rendimientoplanta.presentacion.viewmodel.RecesoVM
import com.example.rendimientoplanta.vo.Resource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_motivo.*
import kotlinx.android.synthetic.main.bottom_sheet_motivo.progress_horizontal
import kotlinx.android.synthetic.main.fragment_jornada.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*


class BottomSheetMotivos : BottomSheetDialogFragment(), AdapterMotivo.OnMotivoClickListenerTrash {
    private lateinit var adapterMotivo: AdapterMotivo
    private val TAG = "BottomSheetMotivos"

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            RecesoFactory(RecesoCase(RecesoRepo()))
        ).get(RecesoVM::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_motivo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().toolbar.subtitle = "Recesos"
        requireActivity().bottom_navigation.isVisible = true
        requireActivity().constaintLayout_toolbar.isVisible = true
    }

    override fun onStart() {
        super.onStart()
        imgExpand.setOnClickListener { dismiss() }
        setAdapter()
        getReceso()
    }

    fun showProgressBar(show: Boolean, progress_horizontal: ProgressBar) {
        progress_horizontal.isVisible = show
    }

    fun setAdapter(){
        adapterMotivo = AdapterMotivo(requireContext(), this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapterMotivo
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    fun getReceso(){
        viewModel.getReceso(Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    Log.w(TAG, "Obteniendo recesos...")
                    showProgressBar(true, progress_horizontal)
                }
                is Resource.Success -> {
                    Log.w(TAG, "Recesos obtenidos ${result.data}")
                    setRecyclerView(result.data)
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure ->{
                    findNavController().navigate(
                        BottomSheetMotivosDirections
                            .actionBottomSheetMotivosToMessageBottomSheet("Error al obtener los recesos del día de hoy.\nMotivo: ${result.exception.message}")
                    )
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    private fun setRecyclerView(data: ArrayList<Motivo>) {
        if(data.size!=0){
            data.sortWith { o1, o2 ->
                Integer.parseInt("${o1!!.horaInicio.split(":")[0]}${o1.horaInicio.split(":")[1]}")
                    .compareTo(Integer.parseInt("${o2!!.horaInicio.split(":")[0]}${o2.horaInicio.split(":")[1]}"))
            }
            adapterMotivo.setListData(data)
            adapterMotivo.notifyDataSetChanged()
        }
    }

    override fun onItemClickTrash(motivo: Motivo) {
        viewModel.delReceso(motivo).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Eliminando recesos...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Receso eliminado ${result.data}")
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure ->{
                    showProgressBar(false, progress_horizontal)
                    findNavController().navigate(
                        BottomSheetMotivosDirections
                            .actionBottomSheetMotivosToMessageBottomSheet("Error al eliminar el receso seleccionado.\nMotivo: ${result.exception.message}")
                    )
                }
            }
        })
        setAdapter()
        getReceso()
    }


}