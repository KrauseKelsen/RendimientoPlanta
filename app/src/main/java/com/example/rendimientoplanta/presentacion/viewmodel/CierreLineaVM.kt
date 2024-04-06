package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.domain.idomain.ICierreLineaCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class CierreLineaVM (iCierreLineaCase: ICierreLineaCase): ViewModel() {
    private var piCierreLineaCase = iCierreLineaCase

    fun getLineas() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piCierreLineaCase.GET_Lineas())
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
}