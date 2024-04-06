package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.domain.idomain.ILogoutCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class LogoutVM (iLogoutCase: ILogoutCase): ViewModel() {
    private var piLogoutCase = iLogoutCase

    fun setToken(user: User, dispositivo: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLogoutCase.PUT_Token(user, dispositivo))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
}