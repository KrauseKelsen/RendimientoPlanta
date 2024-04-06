package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.base.pojos.Operario
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.domain.idomain.ISacarLineaCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class SacarLineaVM (iSacarLineaCase: ISacarLineaCase): ViewModel() {
    private var piSacarLineaCase = iSacarLineaCase
    /**
     * Devuelve un valor
     * Aquí se llaman las corutinas que emiten con emit() el resultado
     */

    fun sacarLinea(
        operarioLinea: OperarioLinea, tallosAsignados: ArrayList<TallosAsignados>, cantidad: Int, motivo: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piSacarLineaCase.PUT_SACAR_LINEA(operarioLinea, tallosAsignados, cantidad, motivo))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
}