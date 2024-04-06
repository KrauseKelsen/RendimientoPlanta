package com.example.rendimientoplanta.domain.idomain

import com.example.rendimientoplanta.base.pojos.Seguridad
import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.vo.Resource

interface IBaseCase {
    suspend fun GET_Permisos(rol: String): Resource<Permiso>
    suspend fun GET_Fincas(): Resource<ArrayList<Finca>>
    suspend fun GET_Lineas(): Resource<ArrayList<Linea>>
    suspend fun GET_Token(dispositivo: String): Resource<User>
    suspend fun PUT_User(user: User, changePassword: Boolean, newPassword: String): Resource<Boolean>
    suspend fun GET_Seguridad(modulo: String): Resource<ArrayList<Seguridad>>
    suspend fun PUT_Rendimiento(user: User, valor: Int, rendimiento: Rendimiento, bandera: Boolean): Resource<Rendimiento>
    suspend fun GET_Rendimiento(user: User): Resource<Rendimiento>
    suspend fun GET_RendimientoPorHora(operarioLinea: OperarioLinea, rendimiento: Rendimiento): Resource<String>
}