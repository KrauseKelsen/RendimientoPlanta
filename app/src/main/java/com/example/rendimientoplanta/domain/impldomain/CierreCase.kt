package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.pojos.CierreLinea
import com.example.rendimientoplanta.base.pojos.CierreLineaLoad
import com.example.rendimientoplanta.base.pojos.Rendimiento
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.data.irepository.ICierreRepo
import com.example.rendimientoplanta.domain.idomain.ICierreCase
import com.example.rendimientoplanta.vo.Resource

class CierreCase(val repo: ICierreRepo): ICierreCase{
    override suspend fun SET_CierreLinea(user: User, horaInicio: String): Resource<CierreLinea> = repo.SET_CierreLinea(user, horaInicio)
    override suspend fun GET_CierreLineaAbierto(user: User): Resource<ArrayList<CierreLinea>> = repo.GET_CierreLineaAbierto(user)
    override suspend fun SET_CierreLinea(user: User,cierreLinea: CierreLinea, horaFin: String, rendimiento: Rendimiento): Resource<CierreLinea> =
        repo.SET_CierreLinea(user, cierreLinea, horaFin, rendimiento)
    override suspend fun GET_CierreLineaCerrado(user: User): Resource<ArrayList<CierreLineaLoad>> = repo.GET_CierreLineaCerrado(user)
}