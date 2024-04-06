package com.example.rendimientoplanta.data.irepository

import com.example.rendimientoplanta.base.pojos.CierreLinea
import com.example.rendimientoplanta.base.pojos.CierreLineaLoad
import com.example.rendimientoplanta.base.pojos.Rendimiento
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.vo.Resource

interface ICierreRepo {
    suspend fun SET_CierreLinea(user: User, horaInicio: String): Resource<CierreLinea>
    suspend fun GET_CierreLineaAbierto(user: User): Resource<ArrayList<CierreLinea>>
    suspend fun SET_CierreLinea(user: User,cierreLinea: CierreLinea, horaFin: String, rendimiento: Rendimiento): Resource<CierreLinea>
    suspend fun GET_CierreLineaCerrado(user: User): Resource<ArrayList<CierreLineaLoad>>
}