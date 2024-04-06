package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.domain.idomain.ICierreOperarioCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class CierreOperarioVM (iCierreOperarioCase: ICierreOperarioCase): ViewModel() {
    private var piCierreOperarioCase = iCierreOperarioCase

    fun getTallosAsignados(cierreLinea: CierreLinea, estado: Boolean) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piCierreOperarioCase.GET_TallosAsignados(cierreLinea, estado))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getRecesos(user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piCierreOperarioCase.GET_Recesos(user))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getCierreOperario(cierreLinea: CierreLinea, operarioId: Int) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piCierreOperarioCase.GET_CierreOperario(cierreLinea, operarioId))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun putCierreOperario(
        user: User,
        tallosParciales: Int,
        tallosAsignados: ArrayList<TallosAsignados>,
        operarioId: Int,
        cierreLinea: CierreLinea,
        recesos: ArrayList<Receso>,
        rendimiento: Rendimiento
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piCierreOperarioCase.PUT_CierreOperario(user, tallosParciales, tallosAsignados, operarioId, cierreLinea, recesos, rendimiento))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getCierresOperarios(cierreLinea: CierreLineaLoad) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piCierreOperarioCase.GET_CierresOperarios(cierreLinea))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
}