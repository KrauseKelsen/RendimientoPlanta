package com.example.rendimientoplanta.presentacion.ui.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.pojos.Operario
import com.example.rendimientoplanta.data.repository.OperarioRepo
import com.example.rendimientoplanta.domain.impldomain.OperarioCase
import com.example.rendimientoplanta.presentacion.adapter.AdapterOperarios
import com.example.rendimientoplanta.presentacion.factory.OperarioFactory
import com.example.rendimientoplanta.presentacion.ui.fragment.RegistroUsuarioFragmentDirections
import com.example.rendimientoplanta.presentacion.viewmodel.OperarioVM
import com.example.rendimientoplanta.vo.Resource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_operarios.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*


class BottomSheetOperarios : BottomSheetDialogFragment() , AdapterOperarios.OnOperariosClickListenerTrash,
    AdapterOperarios.OnOperariosClickListenerDisable, SearchView.OnQueryTextListener {
    private lateinit var adapterOperarios: AdapterOperarios
    private val TAG = "BottomSheetOperarios"
    private val args: BottomSheetOperariosArgs by navArgs()

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            OperarioFactory(OperarioCase(OperarioRepo()))
        ).get(OperarioVM::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_operarios, container, false)
    }

    fun showProgressBar(show: Boolean, progress_horizontal: ProgressBar) {
        progress_horizontal.isVisible = show
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().toolbar.subtitle = "Registro de operario"
        requireActivity().bottom_navigation.isVisible = true
        requireActivity().constaintLayout_toolbar.isVisible = true
    }
    override fun onStart() {
        super.onStart()
        imgExpand.setOnClickListener { dismiss() }
        setAdapter()
        getOperarios()
    }

    fun setAdapter(){
        adapterOperarios = AdapterOperarios(requireContext(), this , this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapterOperarios
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    fun getOperarios(){
        viewModel.getOperarios().observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    Log.w(TAG, "Obteniendo operarios...")
                    showProgressBar(true, progress_horizontal)
                }
                is Resource.Success -> {
                    Log.w(TAG, "Operarios obtenidos ${result.data}")
                    setRecyclerView(result.data)
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure ->{
                    findNavController().navigate(BottomSheetOperariosDirections
                        .actionBottomSheetOperariosToMessageBottomSheet("Error al obtener los operarios.\n${result.exception.message}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    private fun setRecyclerView(data: ArrayList<Operario>) {
        if(data.size!=0){
            data.sortWith { o1, o2 -> o1.nombre.compareTo(o2.nombre) }
            adapterOperarios.setListData(data)
            adapterOperarios.notifyDataSetChanged()
        }
        initListener()
    }

    override fun onItemClickTrash(operario: Operario) {
        findNavController().navigate(BottomSheetOperariosDirections
            .actionBottomSheetOperariosToMessageBottomSheet("Esta opción está deshabilitada en la aplicación."))
    }

    override fun onItemClickDisable(operario: Operario) {
        operario.estado = !operario.estado
        viewModel.putOperario(operario.identificacion, operario.codigo,
            operario.nombre, operario.apellidos, operario.estado, operario.ocupado, args.user!!).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.w(TAG, "Actualizando operario...")
                    showProgressBar(true, progress_horizontal)
                }
                is Resource.Success -> {
                    setAdapter()
                    getOperarios()
                    Log.w(TAG, "Operario actualizado")
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure -> {
                    findNavController().navigate(
                        RegistroUsuarioFragmentDirections
                        .actionRegistroUsuarioFragmentToMessageBottomSheet
                            ("Error al actualizar el operario. \nMotivo_: ${result.exception.message}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    fun initListener(){
        svSearchView.setOnQueryTextListener(this)
    }

    //se ejecuta cuando se presiona el texto
    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    //queda escuchando cada letra
    override fun onQueryTextChange(newText: String?): Boolean {
        adapterOperarios.filter(newText!!)
        return false
    }


}