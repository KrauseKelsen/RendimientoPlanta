package com.example.rendimientoplanta.presentacion.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.utils.instances.BundleActivity
import com.example.rendimientoplanta.data.repository.StartRepo
import com.example.rendimientoplanta.domain.impldomain.StartCase
import com.example.rendimientoplanta.presentacion.factory.StartFactory
import com.example.rendimientoplanta.presentacion.ui.dialogs.DialogConfig
import com.example.rendimientoplanta.presentacion.viewmodel.StartVM
import com.example.rendimientoplanta.vo.Resource
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_load.*
import kotlinx.android.synthetic.main.fragment_start.*
import kotlinx.android.synthetic.main.fragment_start.progress_horizontal
import kotlinx.android.synthetic.main.fragment_start.tvLoading
import java.text.SimpleDateFormat
import java.util.*

class StartFragment : BaseFragment(false, false, "Start") {
    private val TAG = "StartFragment"

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            StartFactory(StartCase(StartRepo()))
        ).get(StartVM::class.java)
    }

    override fun getViewID():Int = R.layout.fragment_start

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getToken(dispositivo).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    messageFragment(TAG, "${MessageBuilder.sLoading} Obteniendo usuario logeado por medio del dispositivo $dispositivo",
                        tvLoading, "Verificando usuario...")
                    showProgressBar(true, progress_horizontal)
                }
                is Resource.Success -> {
                    BundleActivity.updateBundle(requireActivity(), result.data)
                    user = requireActivity().intent.getParcelableExtra("User")!!
                    messageFragment(TAG, "Usuario ${user.nombre} ${user.apellidos} obtenido logeado",
                        tvLoading, "Usuario verificado...")
                    getRendimiento()
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure -> {
                    showProgressBar(false, progress_horizontal)
                    messageFragment(TAG, "Usuario ${user.nombre} ${user.apellidos} obtenido logeado",
                        tvLoading, "Usuario verificado...")
                    when (result.exception) {
                        is IllegalArgumentException -> findNavController().navigate(StartFragmentDirections.actionStartFragmentToLoginFragment())
                        is FirebaseAuthEmailException -> findNavController().navigate(StartFragmentDirections.actionStartFragmentToLoginFragment())
                        is FirebaseAuthInvalidCredentialsException -> {
                            if(result.exception.message.toString()=="DISABLE"){
                                messageFragment(TAG, "Usuario deshabilitado",
                                    tvLoading, "Comuníquese con el administrador...")
                                findNavController().navigate(StartFragmentDirections
                                    .actionStartFragmentToMessageBottomSheet
                                        ("${MessageBuilder.sErrorLogin} ${MessageBuilder.sdisable} su usuario, solicite al administrador para que sea habilitado nuevamente."))
                            }
                            else
                                findNavController().navigate(StartFragmentDirections.actionStartFragmentToLoginFragment())
                        }
                        else -> findNavController().navigate(StartFragmentDirections.actionStartFragmentToMessageBottomSheet("${MessageBuilder.sErrorLogin} ${MessageBuilder.scloseApp}"))
                    }
                }
            }
        })
    }

    private fun getRendimiento() {
        viewModel.getRendimiento(user).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    messageFragment(TAG, "Obteniendo el rendimiento de la finca ${user.finca}, línea ${user.linea}", tvLoading, "Obteniendo el rendimiento...")
                }
                is Resource.Success -> {
                    messageFragment(TAG, "Rendimiento de la finca ${user.finca}, línea ${user.linea} obtenido", tvLoading, "Rendimiento obtenido...")
                    BundleActivity.updateBundle(requireActivity(), result.data)
                    rendimiento = requireActivity().intent.getParcelableExtra("Rendimiento")!!
                    cargarVista(user, "StartFragment")
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure -> {
                    when(result.exception){
                        is NumberFormatException -> {
                            updRendimiento(0, true)
                        }
                        else ->
                            messageFragment(TAG, "Error no se pudo actualizar el rendimiento; Motivo__: ${result.exception.message}",
                                tvLoading, "Error al actualizar el rendimiento...")
                    }
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }
}