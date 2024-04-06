package com.example.rendimientoplanta.data.irepository

import com.example.rendimientoplanta.base.pojos.Motivo
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.vo.Resource

interface IRecesoRepo {
    suspend fun PUT_Receso(user: User, motivo: String, horaInicio: String, horaFin: String): Resource<Boolean>
    suspend fun GET_Receso(dispositivo: String): Resource<ArrayList<Motivo>>
    suspend fun DEL_Receso(motivo: Motivo): Resource<Boolean>
}