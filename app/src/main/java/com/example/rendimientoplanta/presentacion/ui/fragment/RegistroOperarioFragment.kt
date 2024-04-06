package com.example.rendimientoplanta.presentacion.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.utils.validators.FieldDecimalValidators
import com.example.rendimientoplanta.base.utils.validators.FieldValidators
import com.example.rendimientoplanta.data.repository.OperarioRepo
import com.example.rendimientoplanta.domain.impldomain.OperarioCase
import com.example.rendimientoplanta.presentacion.factory.OperarioFactory
import com.example.rendimientoplanta.presentacion.viewmodel.OperarioVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_registrar_operario.*
import kotlinx.android.synthetic.main.fragment_registrar_operario.progress_horizontal

class RegistroOperarioFragment : BaseFragment(true, true, "Registro de operario"){
    private val TAG = "RegistroOperarioFragment"
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            OperarioFactory(OperarioCase(OperarioRepo()))
        ).get(OperarioVM::class.java)
    }

    override fun getViewID():Int = R.layout.fragment_registrar_operario

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showProgressBar(false, progress_horizontal)
        btnVer.setOnClickListener { findNavController().navigate(RegistroOperarioFragmentDirections.actionRegistroOperarioFragmentToBottomSheetOperarios(user)) }
        btnGuardar.setOnClickListener { validarDatos() }
        btnCargar.setOnClickListener { cargarOperariosSQL() }
    }

    private fun validarDatos() {
        if(FieldValidators.isValidate(textInputLayout_identificacion, edit_text_identificacion, resources, "Campo requerido *") &&
            FieldDecimalValidators.validateRango(textInputLayout_codigoOperario, edit_text_codigoOperario, resources, 1000, 9999) &&
            FieldValidators.isValidate(textInputLayout_nombreOperario, edit_text_nombreOperario, resources, "Campo requerido *") &&
            FieldValidators.isValidate(textInputLayout_apellidosOperario, edit_text_apellidosOperario, resources, "Campo requerido *")){
            guardarDatos()
        }
    }

    private fun guardarDatos() {
        viewModel.putOperario(edit_text_identificacion.text.toString(), edit_text_codigoOperario.text.toString().toInt(),
        edit_text_nombreOperario.text.toString(), edit_text_apellidosOperario.text.toString(), true, false, user).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Registrando operario...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Operario registrado")
                    showProgressBar(false, progress_horizontal)
                    limpiarCampos()
                    findNavController().navigate(RegistroOperarioFragmentDirections.actionRegistroOperarioFragmentToMessageBottomSheet("${result.data}"))
                }
                is Resource.Failure -> {
                    findNavController().navigate(RegistroUsuarioFragmentDirections
                        .actionRegistroUsuarioFragmentToMessageBottomSheet
                            ("Error al registrar el operario. \nMotivo_: ${result.exception.message}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    fun limpiarCampos(){
        edit_text_identificacion.text!!.clear()
        edit_text_codigoOperario.text!!.clear()
        edit_text_nombreOperario.text!!.clear()
        edit_text_apellidosOperario.text!!.clear()
    }

    private fun cargarOperariosSQL() {
        viewModel.getOperariosSQL(user).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Registrando operario...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Operarios registrados")
                    showProgressBar(false, progress_horizontal)
                    findNavController().navigate(RegistroOperarioFragmentDirections.actionRegistroOperarioFragmentToMessageBottomSheet("Operarios cargados"))
                }
                is Resource.Failure -> {
                    findNavController().navigate(RegistroUsuarioFragmentDirections
                        .actionRegistroUsuarioFragmentToMessageBottomSheet
                            ("Error al registrar el operario. \nMotivo_: ${result.exception.message}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }
}