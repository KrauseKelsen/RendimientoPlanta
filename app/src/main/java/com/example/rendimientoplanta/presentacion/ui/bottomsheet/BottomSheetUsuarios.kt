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
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.data.repository.UsuarioRepo
import com.example.rendimientoplanta.domain.impldomain.UsuarioCase
import com.example.rendimientoplanta.presentacion.adapter.AdapterUsuarios
import com.example.rendimientoplanta.presentacion.factory.UsuarioFactory
import com.example.rendimientoplanta.presentacion.viewmodel.UsuarioVM
import com.example.rendimientoplanta.vo.Resource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_operarios.*
import kotlinx.android.synthetic.main.bottom_sheet_usuarios.*
import kotlinx.android.synthetic.main.bottom_sheet_usuarios.imgExpand
import kotlinx.android.synthetic.main.bottom_sheet_usuarios.progress_horizontal
import kotlinx.android.synthetic.main.bottom_sheet_usuarios.recyclerView
import kotlinx.android.synthetic.main.bottom_sheet_usuarios.svSearchView
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*


class BottomSheetUsuarios : BottomSheetDialogFragment(),
    AdapterUsuarios.OnUsuariosClickListenerDisable, SearchView.OnQueryTextListener {
    private lateinit var adapterUsuarios: AdapterUsuarios
    private val TAG = "BottomSheetUsuarios"
    private val args: BottomSheetUsuariosArgs by navArgs()

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            UsuarioFactory(UsuarioCase(UsuarioRepo()))
        ).get(UsuarioVM::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_usuarios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().toolbar.subtitle = "Registro de usuarios"
        requireActivity().bottom_navigation.isVisible = true
        requireActivity().constaintLayout_toolbar.isVisible = true
    }
    override fun onStart() {
        super.onStart()
        imgExpand.setOnClickListener { dismiss() }
        setAdapter()
        getUsuarios()
    }

    fun setAdapter(){
        adapterUsuarios = AdapterUsuarios(requireContext(), this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapterUsuarios
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    fun getUsuarios(){
        viewModel.getUsuarios().observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Obteniendo usuarios...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Usuarios obtenidos ${result.data}")
                    setRecyclerView(result.data)
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure ->{
                    findNavController().navigate(BottomSheetUsuariosDirections
                        .actionBottomSheetUsuariosToMessageBottomSheet("Error al obtener los usuarios.\n${result.exception.message}"))
                    showProgressBar(false, progress_horizontal)
                }

            }
        })
    }

    private fun setRecyclerView(data: ArrayList<User>) {
        if(data.size!=0){
            data.sortWith { o1, o2 -> o1.nombre.compareTo(o2.nombre) }
            adapterUsuarios.setListData(data)
            adapterUsuarios.notifyDataSetChanged()
        }
        initListener()
    }

    override fun onItemClickDisable(usuario: User) {
        if(usuario.uid == args.user!!.uid) findNavController().navigate(BottomSheetUsuariosDirections
            .actionBottomSheetUsuariosToMessageBottomSheet("Usted no se puede deshabilitar a sí mismo"))
        else{
            disableUser(usuario)
        }
    }

    fun disableUser(usuario: User){
        usuario.estado = !usuario.estado
        viewModel.putUsuarios(usuario.uid, usuario.nombre, usuario.apellidos,
            usuario.email, usuario.rol, usuario.finca, "Línea ${usuario.linea}", usuario.estado).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)

                    Log.w(TAG, "Actualizando usuario...")
                }
                is Resource.Success -> {
                    setAdapter()
                    getUsuarios()
                    Log.w(TAG, "User actualizado")
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure -> {
                    findNavController().navigate(BottomSheetUsuariosDirections
                        .actionBottomSheetUsuariosToMessageBottomSheet("Error al actualizar los datos del usuario.\n${result.exception.message}"))
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
        adapterUsuarios.filter(newText!!)
        return false
    }

    fun showProgressBar(show: Boolean, progress_horizontal: ProgressBar) {
        progress_horizontal.isVisible = show
    }
}