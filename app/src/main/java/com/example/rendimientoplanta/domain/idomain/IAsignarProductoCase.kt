package com.example.rendimientoplanta.domain.idomain

import com.example.rendimientoplanta.base.pojos.Jornada
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.vo.Resource

interface IAsignarProductoCase {
    suspend fun GET_Jornada(user: User): Resource<ArrayList<Jornada>>
    suspend fun GET_OperariosEnLinea(user: User): Resource<ArrayList<OperarioLinea>>
    suspend fun GET_Stems(codigo: Int, user: User): Resource<ArrayList<TallosAsignados>>
    suspend fun SET_Stems(
        operarioLinea: OperarioLinea,
        user: User,
        cantidad: Int,
        jornada: Jornada,
        tipoTalloSeleccionado: String
    ): Resource<Boolean>
    suspend fun SET_Stem(array: ArrayList<TallosAsignados>): Resource<Boolean>
}