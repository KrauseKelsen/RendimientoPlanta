package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.domain.idomain.IDesasignarProductoCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class DesasignarProductoVM (iDesasignarProductoCase: IDesasignarProductoCase): ViewModel() {
    private var piDesasignarProductoCase = iDesasignarProductoCase

    fun getOperariosEnLinea(user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piDesasignarProductoCase.GET_OperariosEnLinea(user))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getStems(codigo: Int, user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piDesasignarProductoCase.GET_Stems(codigo, user))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun setStems(operarioLinea: OperarioLinea, user: User, cantidad: Int, motivo: String, tallosAsignados: ArrayList<TallosAsignados> ) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piDesasignarProductoCase.SET_Stems(operarioLinea, user, cantidad, motivo, tallosAsignados))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
}