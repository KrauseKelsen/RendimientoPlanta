package com.example.rendimientoplanta.domain.idomain

import com.example.rendimientoplanta.base.pojos.Operario
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.vo.Resource

interface IOperarioCase {
    suspend fun GET_OperarioLinea(codigo: Int): Resource<OperarioLinea>
    suspend fun GET_OperariosIngresados(user: User): Resource<ArrayList<OperarioLinea>>
    suspend fun GET_Operario(codigo: Int): Resource<Operario>
    suspend fun GET_Operarios(): Resource<ArrayList<Operario>>
    suspend fun SET_OperarioEnLinea(codigo: Int, user: User): Resource<Boolean>
    suspend fun GET_Stems(): Resource<ArrayList<TallosAsignados>>
    suspend fun UPD_OperarioLinea(operario: OperarioLinea): Resource<Boolean>
    suspend fun UPD_TallosAsignados(operarioLinea: OperarioLinea): Resource<String>
    suspend fun PUT_Operario(identificacion: String, codigo: Int, nombre: String, apellido: String, estado: Boolean, ocupado: Boolean, user: User): Resource<String>
    suspend fun GET_OperariosSQL(user: User): Resource<ArrayList<Operario>>
}