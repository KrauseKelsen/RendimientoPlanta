package com.example.rendimientoplanta.presentacion.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.data.repository.LogoutRepo
import com.example.rendimientoplanta.domain.impldomain.LogoutCase
import com.example.rendimientoplanta.presentacion.factory.LogoutFactory
import com.example.rendimientoplanta.presentacion.viewmodel.LogoutVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_logout.*

class LogoutFragment : BaseFragment(false, false, "Logout") {
    private val TAG = "LogoutFragment"
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            LogoutFactory(LogoutCase(LogoutRepo()))
        ).get(LogoutVM::class.java)
    }

    override fun getViewID():Int = R.layout.fragment_logout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setToken(requireActivity().intent.getParcelableExtra("User")!!)
    }

    private fun setToken(user: User) {
        viewModel.setToken(user, dispositivo)
            .observe(viewLifecycleOwner, { result ->
                when(result){
                    is Resource.Loading -> {
                        Log.w(TAG, "${MessageBuilder.sLoading} Seteando el token...")
                    }
                    is Resource.Success -> {
                        this.progress_horizontal.isVisible = false
                        findNavController().navigate(R.id.startFragment)
                    }
                    is Resource.Failure -> {
                        this.progress_horizontal.isVisible = false
                        findNavController().navigate(LogoutFragmentDirections.actionLogoutFragmentToMessageBottomSheet("Error al guardar el token de su sesión, ${MessageBuilder.scloseApp}"))
                    }
                }
            })
    }
}