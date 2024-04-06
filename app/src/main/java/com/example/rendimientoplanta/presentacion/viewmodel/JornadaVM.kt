package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.domain.idomain.IJornadaCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class JornadaVM (iJornadaCase: IJornadaCase): ViewModel() {
    private var piJornadaCase = iJornadaCase
    fun setJornada(user: User, horaInicio: String, horaFin: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piJornadaCase.PUT_Jornada(user, horaInicio, horaFin))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun getJornada(user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piJornadaCase.GET_Jornada(user))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
}