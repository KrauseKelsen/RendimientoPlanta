package com.example.rendimientoplanta.presentacion.ui.fragment

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.base.utils.instances.BundleActivity
import com.example.rendimientoplanta.base.utils.validators.FieldLoginValidators.invalidEmail
import com.example.rendimientoplanta.base.utils.validators.FieldLoginValidators.invalidPassword
import com.example.rendimientoplanta.base.utils.validators.FieldLoginValidators.isValidate
import com.example.rendimientoplanta.data.repository.LoginRepo
import com.example.rendimientoplanta.domain.impldomain.LoginCase
import com.example.rendimientoplanta.presentacion.factory.LoginFactory
import com.example.rendimientoplanta.presentacion.viewmodel.LoginVM
import com.example.rendimientoplanta.vo.Resource
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseFragment(false, false, "Login") {
    private val TAG = "LoginFragment"
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            LoginFactory(LoginCase(LoginRepo()))
        ).get(LoginVM::class.java)
    }

    override fun getViewID(): Int = R.layout.fragment_login

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showProgressBar(false, progress_horizontal)
        forgetPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        forgetPassword.setOnClickListener { Log.w(TAG, "onViewCreated: Olvidó contraseña") }
        bt_iniciarSesion.setOnClickListener { if (isValidate(textInputLayout_email, text_email, textInputLayout_password, text_password, resources)) login() }
    }

    private fun login() {
        bt_iniciarSesion.isEnabled = false
        viewModel.login(text_email.text.toString(), text_password.text.toString())
            .observe(viewLifecycleOwner, { result ->
                when (result) {
                    is Resource.Loading -> {
                        Log.w(TAG, "${MessageBuilder.sLoading} Verificando usuario...")
                        showProgressBar(true, progress_horizontal)
                    }
                    is Resource.Success -> {
                        bt_iniciarSesion.isEnabled = true
                        if(result.data.estado){
                            Log.w(TAG,"${MessageBuilder.sSuccess} Usuario verificado como_: ID -> ${result.data.uid}, Email -> ${result.data.email}")
                            BundleActivity.updateBundle(requireActivity(), result.data)
                            setToken(requireActivity().intent.getParcelableExtra("User")!!)
                        }else{
                            findNavController().navigate(LoginFragmentDirections
                                .actionLoginFragmentToMessageBottomSheet
                                    ("${MessageBuilder.sErrorLogin} ${MessageBuilder.sdisable} su usuario, solicite al administrador para que sea habilitado nuevamente."))
                        }
                        showProgressBar(false, progress_horizontal)
                    }
                    is Resource.Failure -> {
                        bt_iniciarSesion.isEnabled = true
                        when (result.exception) {
                            is IllegalArgumentException -> invalidEmail(TAG,"${MessageBuilder.sFailure} Usuario no autenticado_: ${result.exception.message}")
                            is FirebaseAuthInvalidCredentialsException -> invalidPassword(TAG,"${MessageBuilder.sFailure} Usuario no autenticado_: ${result.exception.message}")
                            else -> {
                                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMessageBottomSheet("${MessageBuilder.sErrorLogin} ${MessageBuilder.scloseApp}"))
                            }
                        }
                        showProgressBar(false, progress_horizontal)
                    }
                }
            })
    }

    private fun setToken(user: User) {
        viewModel.setToken(user, dispositivo)
            .observe(viewLifecycleOwner, { result ->
                when(result){
                    is Resource.Loading -> {
                        Log.w(TAG, "${MessageBuilder.sLoading} Seteando el token...")
                        showProgressBar(true, progress_horizontal)
                    }
                    is Resource.Success -> {
                        getRendimiento(user)
                        showProgressBar(false, progress_horizontal)
                    }
                    is Resource.Failure -> {
                        bt_iniciarSesion.isClickable = true
                        findNavController().navigate(LoginFragmentDirections
                            .actionLoginFragmentToMessageBottomSheet("Error al guardar el token de su sesión, ${MessageBuilder.scloseApp}"))
                        showProgressBar(false, progress_horizontal)
                    }
                }
        })
    }

    private fun getRendimiento(user: User) {
        viewModel.getRendimiento(user).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Obteniendo el rendimiento...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Rendimiento obtenido ${result.data}")
                    BundleActivity.updateBundle(requireActivity(), result.data)
                    rendimiento = requireActivity().intent.getParcelableExtra("Rendimiento")!!
                    bt_iniciarSesion.isClickable = true
                    cargarVista(this.user, "LoginFragment")
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure -> {
                    when(result.exception){
                        is NumberFormatException -> {
                            updRendimiento(0, true)
                        }
                        else -> findNavController().navigate(LoginFragmentDirections
                            .actionLoginFragmentToMessageBottomSheet
                                ("${MessageBuilder.sErrorLogin} no se puedo obtener el rendimiento de la finca y línea configurada de su usuario, " +
                                    "comuníquese con soporte."))
                    }
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }
}