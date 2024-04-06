package com.example.rendimientoplanta.data.irepository

import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.vo.Resource

interface ISacarLineaRepo {
    suspend fun PUT_SACAR_LINEA(operarioLinea: OperarioLinea, tallosAsignados: ArrayList<TallosAsignados>, cantidad: Int, motivo: String): Resource<Boolean>
}