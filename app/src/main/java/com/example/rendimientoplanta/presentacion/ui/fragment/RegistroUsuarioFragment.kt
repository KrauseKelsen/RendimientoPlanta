package com.example.rendimientoplanta.presentacion.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.builder.StringBuilder
import com.example.rendimientoplanta.base.pojos.Finca
import com.example.rendimientoplanta.base.utils.validators.FieldLoginValidators
import com.example.rendimientoplanta.base.utils.validators.FieldValidators
import com.example.rendimientoplanta.data.repository.UsuarioRepo
import com.example.rendimientoplanta.domain.impldomain.UsuarioCase
import com.example.rendimientoplanta.presentacion.factory.UsuarioFactory
import com.example.rendimientoplanta.presentacion.viewmodel.UsuarioVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_registrar_usuario.*
import kotlinx.android.synthetic.main.fragment_registrar_usuario.btnGuardar
import kotlinx.android.synthetic.main.fragment_registrar_usuario.btnVer
import kotlinx.android.synthetic.main.fragment_registrar_usuario.edit_text_apellidosOperario
import kotlinx.android.synthetic.main.fragment_registrar_usuario.edit_text_nombreOperario
import kotlinx.android.synthetic.main.fragment_registrar_usuario.progress_horizontal
import kotlinx.android.synthetic.main.fragment_registrar_usuario.textInputLayout_apellidosOperario
import kotlinx.android.synthetic.main.fragment_registrar_usuario.textInputLayout_nombreOperario
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RegistroUsuarioFragment : BaseFragment(true, true, "Registro de usuarios"){
    private val TAG = "RegistroUsuarioFragment"
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            UsuarioFactory(UsuarioCase(UsuarioRepo()))
        ).get(UsuarioVM::class.java)
    }

    private var miFinca = ""
    private var miLinea = ""
    private var miRol = ""
    private var misRoles = ArrayList<String>()
    private var misFincas = ArrayList<String>()
    private var misFincasObj = ArrayList<Finca>()
    private var misLineas = ArrayList<String>()
    override fun getViewID():Int = R.layout.fragment_registrar_usuario

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getFincasToSpinner()
        showProgressBar(false, progress_horizontal)
        btnVer.setOnClickListener { findNavController().navigate(RegistroUsuarioFragmentDirections.actionRegistroUsuarioFragmentToBottomSheetUsuarios(user))}
        btnGuardar.setOnClickListener { validarDatos() }
    }

    fun getFincasToSpinner(){
        viewModel.getFincas().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Obteniendo fincas...")
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "Fincas obtenidas ${result.data}")
                    misFincasObj = result.data
                    misFincas.addAll(StringBuilder.getStrings(misFincasObj as ArrayList<Any>, "nombre"))
                    spinnerPlantaOnItemSelectedListener()
                    getLineasToSpinner()
                }
                is Resource.Failure -> {
                    findNavController().navigate(RegistroUsuarioFragmentDirections
                        .actionRegistroUsuarioFragmentToMessageBottomSheet
                            ("Error al obtener las fincas. \nMotivo_: ${result.exception.message}"))
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sFailure}, no se pudieron obtener las fincas")
                }
            }
        })
    }

    fun spinnerPlantaOnItemSelectedListener(){
        spinnerPlanta.adapter = ArrayAdapter(requireContext(),
            R.layout.custom_spinner_list_item, misFincas)
        spinnerPlanta.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                miFinca = misFincas[position]

            }
        }
    }

    fun getLineasToSpinner(){
        viewModel.getLineas().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Obteniendo lineas...")
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "Lineas obtenidas ${result.data}")
                    misLineas.addAll(StringBuilder.getStrings(result.data as ArrayList<Any>, "nombre"))
                    spinnerLineaOnItemSelectedListener()
                    getRoles()
                }
                is Resource.Failure -> {
                    findNavController().navigate(RegistroUsuarioFragmentDirections
                        .actionRegistroUsuarioFragmentToMessageBottomSheet
                            ("Error al obtener las línas. \nMotivo_: ${result.exception.message}"))
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sFailure}, no se pudieron obtener las lineas")
                }
            }
        })
    }

    fun spinnerLineaOnItemSelectedListener(){
        spinnerLinea.adapter = ArrayAdapter(requireContext(),
            R.layout.custom_spinner_list_item, misLineas)
        spinnerLinea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                miLinea = misLineas[position]
            }
        }
    }

    fun getRoles(){
        viewModel.getPermisos().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Obteniendo misRoles...")
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)

                    Log.w(TAG, "Roles obtenidos ${result.data}")
                    misRoles = StringBuilder.getStrings(result.data as ArrayList<Any>, "uid")
                    spinnerRolesOnItemSelectedListener()
                }
                is Resource.Failure -> {
                    findNavController().navigate(RegistroUsuarioFragmentDirections
                        .actionRegistroUsuarioFragmentToMessageBottomSheet
                            ("Error al obtener los roles. \nMotivo_: ${result.exception.message}"))
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sFailure}, no se pudieron obtener las fincas")
                }
            }
        })
    }

    fun spinnerRolesOnItemSelectedListener(){
        spinnerRol.adapter = ArrayAdapter(requireContext(),
            R.layout.custom_spinner_list_item, misRoles)
        spinnerRol.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                miRol = misRoles[position]
            }
        }
    }

    private fun validarDatos() {
        if(FieldValidators.isValidate(textInputLayout_nombreOperario, edit_text_nombreOperario, resources, "Campo requerido *") &&
            FieldValidators.isValidate(textInputLayout_apellidosOperario, edit_text_apellidosOperario, resources, "Campo requerido *") &&
            FieldLoginValidators.validateEmail(textInputLayout_email, edit_text_email, resources)){
            when {
                miRol == "" -> {
                    findNavController().navigate(RegistroUsuarioFragmentDirections.actionRegistroUsuarioFragmentToMessageBottomSheet("Debe seleccionar un rol"))
                }
                miFinca == "" -> {
                    findNavController().navigate(RegistroUsuarioFragmentDirections.actionRegistroUsuarioFragmentToMessageBottomSheet("Debe seleccionar una finca"))
                }
                miLinea == "" -> {
                    findNavController().navigate(RegistroUsuarioFragmentDirections.actionRegistroUsuarioFragmentToMessageBottomSheet("Debe seleccionar una línea"))
                }
                else -> {
                    guardarDatos()
                }
            }

        }
    }

    private fun guardarDatos() {
        viewModel.putUsuarios(
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date()), edit_text_nombreOperario.text.toString(),
            edit_text_apellidosOperario.text.toString(), edit_text_email.text.toString(), miRol, miFinca, miLinea, true)
            .observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Registrando usuario...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Usuario registrado")
                    showProgressBar(false, progress_horizontal)
                    limpiarCampos()
                    findNavController().navigate(RegistroUsuarioFragmentDirections.actionRegistroUsuarioFragmentToMessageBottomSheet("${result.data}"))
                }
                is Resource.Failure -> {
                    findNavController().navigate(RegistroUsuarioFragmentDirections
                        .actionRegistroUsuarioFragmentToMessageBottomSheet
                            ("Error al registrar el usuario. \nMotivo_: ${result.exception.message}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    fun limpiarCampos(){
        edit_text_nombreOperario.text!!.clear()
        edit_text_apellidosOperario.text!!.clear()
        edit_text_email.text!!.clear()
    }

}