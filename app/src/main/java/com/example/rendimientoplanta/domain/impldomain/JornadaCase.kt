package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.pojos.Jornada
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.data.irepository.IJornadaRepo
import com.example.rendimientoplanta.domain.idomain.IJornadaCase
import com.example.rendimientoplanta.vo.Resource

class JornadaCase(val repo: IJornadaRepo): IJornadaCase{
    override suspend fun PUT_Jornada(user: User, horaInicio: String, horaFin: String): Resource<Boolean> = repo.PUT_Jornada(user, horaInicio, horaFin)
    override suspend fun GET_Jornada(user: User): Resource<ArrayList<Jornada>> = repo.GET_Jornada(user)
}