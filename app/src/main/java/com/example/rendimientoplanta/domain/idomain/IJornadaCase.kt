package com.example.rendimientoplanta.domain.idomain

import com.example.rendimientoplanta.base.pojos.Jornada
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.vo.Resource

interface IJornadaCase {
    suspend fun PUT_Jornada(user: User, horaInicio: String, horaFin: String): Resource<Boolean>
    suspend fun GET_Jornada(user: User): Resource<ArrayList<Jornada>>
}