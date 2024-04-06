package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.domain.idomain.IStartCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class StartVM (iStartCase: IStartCase): ViewModel() {
    private var piStartCase = iStartCase
    /**
     * Devuelve un valor
     * Aquí se llaman las corutinas que emiten con emit() el resultado
     */
    fun getToken(dispositivo: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piStartCase.GET_Token(dispositivo))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getRendimiento(user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piStartCase.GET_Rendimiento(user))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
}