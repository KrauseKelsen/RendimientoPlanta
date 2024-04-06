package com.example.rendimientoplanta.domain.idomain

import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.vo.Resource

interface ISacarLineaCase {
    suspend fun PUT_SACAR_LINEA(operarioLinea: OperarioLinea, tallosAsignados: ArrayList<TallosAsignados>, cantidad: Int, motivo: String): Resource<Boolean>
}