package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.pojos.Seguridad
import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.data.irepository.IBaseRepo
import com.example.rendimientoplanta.domain.idomain.IBaseCase
import com.example.rendimientoplanta.vo.Resource

class BaseCase(val repo: IBaseRepo): IBaseCase {
    override suspend fun GET_Permisos(rol: String): Resource<Permiso> = repo.GET_Permisos(rol)
    override suspend fun GET_Fincas(): Resource<ArrayList<Finca>> = repo.GET_Fincas()
    override suspend fun GET_Lineas(): Resource<ArrayList<Linea>> = repo.GET_Lineas()
    override suspend fun GET_Token(dispositivo: String): Resource<User> = repo.GET_Token(dispositivo)
    override suspend fun PUT_User(user: User, changePassword: Boolean, newPassword: String): Resource<Boolean> = repo.PUT_User(user, changePassword, newPassword)
    override suspend fun GET_Seguridad(modulo: String): Resource<ArrayList<Seguridad>> = repo.GET_Seguridad(modulo)
    override suspend fun PUT_Rendimiento(user: User, valor: Int, rendimiento: Rendimiento, bandera: Boolean): Resource<Rendimiento> =
        repo.PUT_Rendimiento(user, valor, rendimiento, bandera)
    override suspend fun GET_Rendimiento(user: User): Resource<Rendimiento> = repo.GET_Rendimiento(user)
    override suspend fun GET_RendimientoPorHora( operarioLinea: OperarioLinea, rendimiento: Rendimiento): Resource<String> =
        repo.GET_RendimientoPorHora(operarioLinea, rendimiento)
}