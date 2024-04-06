package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.base.pojos.CierreLinea
import com.example.rendimientoplanta.base.pojos.Operario
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.domain.idomain.ITiempoMuertoCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class TiempoMuertoVM (iTiempoMuertoCase: ITiempoMuertoCase): ViewModel() {
    private var piTiempoMuertoCase = iTiempoMuertoCase

    fun getOperarios(user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piTiempoMuertoCase.GET_Operarios(user))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun setTiempoMuerto(operario: Operario, user: User, horaInicio: String, horaFin: String, motivo: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piTiempoMuertoCase.SET_TiempoMuerto(operario, user, horaInicio, horaFin, motivo))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
}