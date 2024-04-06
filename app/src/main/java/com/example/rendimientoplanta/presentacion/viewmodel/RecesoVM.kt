package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.base.pojos.Motivo
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.domain.idomain.IRecesoCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class RecesoVM (iRecesoCase: IRecesoCase): ViewModel() {
    private var piRecesoCase = iRecesoCase
    fun setReceso(user: User, motivo: String, horaInicio: String, horaFin: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piRecesoCase.PUT_Receso(user, motivo, horaInicio, horaFin))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getReceso(dispositivo: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piRecesoCase.GET_Receso(dispositivo))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun delReceso(motivo: Motivo) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piRecesoCase.DEL_Receso(motivo))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
}