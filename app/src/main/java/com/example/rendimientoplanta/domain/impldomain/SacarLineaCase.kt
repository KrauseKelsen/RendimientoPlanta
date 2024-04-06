package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.pojos.Operario
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.data.irepository.ISacarLineaRepo
import com.example.rendimientoplanta.domain.idomain.ISacarLineaCase
import com.example.rendimientoplanta.vo.Resource

class SacarLineaCase(val repo: ISacarLineaRepo): ISacarLineaCase {
    override suspend fun PUT_SACAR_LINEA(operarioLinea: OperarioLinea, tallosAsignados: ArrayList<TallosAsignados>, cantidad: Int, motivo: String): Resource<Boolean>
    = repo.PUT_SACAR_LINEA(operarioLinea, tallosAsignados, cantidad, motivo)

}