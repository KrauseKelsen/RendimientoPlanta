package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.data.irepository.IOperarioRepo
import com.example.rendimientoplanta.domain.idomain.IOperarioCase
import com.example.rendimientoplanta.vo.Resource

class OperarioCase(val repo: IOperarioRepo): IOperarioCase {
    override suspend fun GET_OperarioLinea(codigo: Int): Resource<OperarioLinea> = repo.GET_OperarioLinea(codigo)
    override suspend fun GET_OperariosIngresados(user: User): Resource<ArrayList<OperarioLinea>> = repo.GET_OperariosIngresados(user)
    override suspend fun GET_Operario(codigo: Int): Resource<Operario> = repo.GET_Operario(codigo)
    override suspend fun GET_Operarios(): Resource<ArrayList<Operario>> = repo.GET_Operarios()
    override suspend fun SET_OperarioEnLinea(codigo: Int, user: User): Resource<Boolean> = repo.SET_OperarioEnLinea(codigo, user)
    override suspend fun GET_Stems(): Resource<ArrayList<TallosAsignados>> = repo.GET_Stems()
    override suspend fun UPD_OperarioLinea(operarioLinea: OperarioLinea): Resource<Boolean> = repo.UPD_OperarioLinea(operarioLinea)
    override suspend fun UPD_TallosAsignados(operarioLinea: OperarioLinea): Resource<String> = repo.UPD_TallosAsignados(operarioLinea)
    override suspend fun PUT_Operario(identificacion: String, codigo: Int, nombre: String, apellido: String, estado: Boolean, ocupado: Boolean, user: User):
            Resource<String> = repo.PUT_Operario(identificacion, codigo, nombre, apellido, estado, ocupado, user)
    override suspend fun GET_OperariosSQL(user: User): Resource<ArrayList<Operario>> = repo.GET_OperariosSQL(user)


}