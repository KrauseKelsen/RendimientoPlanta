package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.base.pojos.Jornada
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.domain.idomain.IAsignarProductoCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class AsignarProductoVM (iAsignarProductoCase: IAsignarProductoCase): ViewModel() {
    private var piAsignarProductoCase = iAsignarProductoCase

    fun getJornada(user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piAsignarProductoCase.GET_Jornada(user))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getOperariosEnLinea(user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piAsignarProductoCase.GET_OperariosEnLinea(user))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getStems(codigo: Int, user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piAsignarProductoCase.GET_Stems(codigo, user))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun setStems(
        operarioLinea: OperarioLinea,
        user: User,
        cantidad: Int,
        jornada: Jornada,
        tipoTalloSeleccionado: String
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piAsignarProductoCase.SET_Stems(operarioLinea, user, cantidad, jornada, tipoTalloSeleccionado))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun setStem(array: ArrayList<TallosAsignados>) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piAsignarProductoCase.SET_Stem(array))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
}