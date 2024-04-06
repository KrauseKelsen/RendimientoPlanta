package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.Rendimiento
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.domain.idomain.IBaseCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class BaseVM (iBaseCase: IBaseCase): ViewModel() {
    private var piBaseCase = iBaseCase

    fun getPermisos(rol: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piBaseCase.GET_Permisos(rol))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getFincas() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piBaseCase.GET_Fincas())
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getLineas() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piBaseCase.GET_Lineas())
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getToken(dispositivo: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piBaseCase.GET_Token(dispositivo))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun putUser(user: User, changePassword: Boolean, newPassword: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piBaseCase.PUT_User(user, changePassword, newPassword))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getSeguridad(modulo: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piBaseCase.GET_Seguridad(modulo))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun updRendimiento(user: User, valor: Int, rendimiento: Rendimiento, bandera: Boolean) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piBaseCase.PUT_Rendimiento(user, valor, rendimiento, bandera))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getRendimiento(user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piBaseCase.GET_Rendimiento(user))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getRendimientoPorHora(operarioLinea: OperarioLinea, rendimiento: Rendimiento) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piBaseCase.GET_RendimientoPorHora(operarioLinea, rendimiento))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
}