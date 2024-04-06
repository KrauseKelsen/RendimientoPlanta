package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.domain.idomain.ILoginCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class LoginVM (iLoginCase: ILoginCase): ViewModel() {
    private var piLoginCase = iLoginCase
    /**
     * Devuelve un valor
     * Aquí se llaman las corutinas que emiten con emit() el resultado
     */
    fun login(email: String, contrasenna: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLoginCase.GET_loginRepo(email, contrasenna))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun setToken(user: User, dispositivo: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLoginCase.PUT_Token(user, dispositivo))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getRendimiento(user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLoginCase.GET_Rendimiento(user))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
}