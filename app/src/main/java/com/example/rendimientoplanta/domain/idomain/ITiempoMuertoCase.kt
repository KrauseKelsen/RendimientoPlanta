package com.example.rendimientoplanta.domain.idomain

import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.vo.Resource

interface ITiempoMuertoCase {
    suspend fun GET_Operarios(user: User): Resource<ArrayList<Operario>>
    suspend fun SET_TiempoMuerto(operario: Operario, user: User, horaInicio: String, horaFin: String, motivo: String): Resource<String>

}