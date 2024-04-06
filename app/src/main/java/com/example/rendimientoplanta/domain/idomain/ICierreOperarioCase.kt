package com.example.rendimientoplanta.domain.idomain

import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.vo.Resource

interface ICierreOperarioCase {
    suspend fun GET_TallosAsignados(cierreLinea: CierreLinea, estado: Boolean): Resource<ArrayList<TallosAsignados>>
    suspend fun GET_Recesos(user: User): Resource<ArrayList<Receso>>
    suspend fun GET_CierreOperario(cierreLinea: CierreLinea, operarioId: Int): Resource<CierreOperario>
    suspend fun GET_CierresOperarios(cierreLinea: CierreLineaLoad): Resource<ArrayList<CierreOperarioLoad>>
    suspend fun PUT_CierreOperario(user: User, tallosParciales: Int, tallosAsignados: ArrayList<TallosAsignados>,
                                   operarioId: Int, cierreLinea: CierreLinea, recesos : ArrayList<Receso>, rendimiento: Rendimiento):
            Resource<String>
}