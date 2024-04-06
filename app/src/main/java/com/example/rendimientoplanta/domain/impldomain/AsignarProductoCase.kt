package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.pojos.Jornada
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.data.irepository.IAsignarProductoRepo
import com.example.rendimientoplanta.domain.idomain.IAsignarProductoCase
import com.example.rendimientoplanta.vo.Resource

class AsignarProductoCase(val repo: IAsignarProductoRepo): IAsignarProductoCase{
    override suspend fun GET_Jornada(user: User): Resource<ArrayList<Jornada>> = repo.GET_Jornada(user)
    override suspend fun GET_OperariosEnLinea(user: User): Resource<ArrayList<OperarioLinea>> = repo.GET_OperariosEnLinea(user)
    override suspend fun GET_Stems(codigo: Int, user: User): Resource<ArrayList<TallosAsignados>> = repo.GET_Stems(codigo, user)
    override suspend fun SET_Stems(
        operarioLinea: OperarioLinea,
        user: User,
        cantidad: Int,
        jornada: Jornada,
        tipoTalloSeleccionado: String
    ) = repo.SET_Stems(operarioLinea, user, cantidad, jornada, tipoTalloSeleccionado)
    override suspend fun SET_Stem(array: ArrayList<TallosAsignados>): Resource<Boolean> = repo.SET_Stem(array)
}