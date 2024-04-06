package com.example.rendimientoplanta.data.irepository

import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.vo.Resource

interface IDesasignarProductoRepo {
    suspend fun GET_OperariosEnLinea(user: User): Resource<ArrayList<OperarioLinea>>
    suspend fun GET_Stems(codigo: Int, user: User): Resource<ArrayList<TallosAsignados>>
    suspend fun SET_Stems(operarioLinea: OperarioLinea, user: User, cantidad: Int, motivo: String, tallosAsignados: ArrayList<TallosAsignados>): Resource<Boolean>
}