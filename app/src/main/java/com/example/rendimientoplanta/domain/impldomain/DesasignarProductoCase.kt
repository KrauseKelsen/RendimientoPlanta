package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.data.irepository.IDesasignarProductoRepo
import com.example.rendimientoplanta.domain.idomain.IDesasignarProductoCase
import com.example.rendimientoplanta.vo.Resource

class DesasignarProductoCase(val repo: IDesasignarProductoRepo): IDesasignarProductoCase{
    override suspend fun GET_OperariosEnLinea(user: User): Resource<ArrayList<OperarioLinea>> = repo.GET_OperariosEnLinea(user)
    override suspend fun GET_Stems(codigo: Int, user: User): Resource<ArrayList<TallosAsignados>> = repo.GET_Stems(codigo, user)
    override suspend fun SET_Stems(operarioLinea: OperarioLinea, user: User, cantidad: Int, motivo: String, tallosAsignados: ArrayList<TallosAsignados>) = repo.SET_Stems(operarioLinea, user, cantidad, motivo, tallosAsignados)
}