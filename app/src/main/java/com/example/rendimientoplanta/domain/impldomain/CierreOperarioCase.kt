package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.data.irepository.ICierreOperarioRepo
import com.example.rendimientoplanta.domain.idomain.ICierreOperarioCase
import com.example.rendimientoplanta.vo.Resource

class CierreOperarioCase(val repo: ICierreOperarioRepo): ICierreOperarioCase {
    override suspend fun GET_TallosAsignados(cierreLinea: CierreLinea, estado: Boolean): Resource<ArrayList<TallosAsignados>> =
        repo.GET_TallosAsignados(cierreLinea, estado)
    override suspend fun GET_Recesos(user: User): Resource<ArrayList<Receso>> = repo.GET_Recesos(user)
    override suspend fun GET_CierreOperario(cierreLinea: CierreLinea, operarioId: Int): Resource<CierreOperario> = repo.GET_CierreOperario(cierreLinea, operarioId)
    override suspend fun GET_CierresOperarios(cierreLinea: CierreLineaLoad): Resource<ArrayList<CierreOperarioLoad>> = repo.GET_CierresOperarios(cierreLinea)
    override suspend fun PUT_CierreOperario(user: User, tallosParciales: Int, tallosAsignados: ArrayList<TallosAsignados>,
                                            operarioId: Int, cierreLinea: CierreLinea, recesos : ArrayList<Receso>, rendimiento: Rendimiento):
            Resource<String> = repo.PUT_CierreOperario(user, tallosParciales, tallosAsignados, operarioId, cierreLinea, recesos, rendimiento)
}