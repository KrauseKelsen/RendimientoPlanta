package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.base.pojos.CierreLinea
import com.example.rendimientoplanta.base.pojos.Rendimiento
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.domain.idomain.ICierreCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class CierreVM (iCierreCase: ICierreCase): ViewModel() {
    private var piCierreCase = iCierreCase

    fun setCierreLinea(user: User, horaInicio: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piCierreCase.SET_CierreLinea(user, horaInicio))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getCierreLineaAbierto(user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piCierreCase.GET_CierreLineaAbierto(user))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun setCierreLinea(user: User,cierreLinea: CierreLinea, horaFin: String, rendimiento: Rendimiento) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piCierreCase.SET_CierreLinea(user, cierreLinea, horaFin, rendimiento))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getCierreLineaCerrado(user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piCierreCase.GET_CierreLineaCerrado(user))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
}