package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.data.irepository.IUsuarioRepo
import com.example.rendimientoplanta.domain.idomain.IUsuarioCase
import com.example.rendimientoplanta.vo.Resource

class UsuarioCase(val repo: IUsuarioRepo): IUsuarioCase {
    override suspend fun GET_Fincas(): Resource<ArrayList<Finca>> = repo.GET_Fincas()
    override suspend fun GET_Lineas(): Resource<ArrayList<Linea>> = repo.GET_Lineas()
    override suspend fun GET_Permisos(): Resource<ArrayList<Permiso>> = repo.GET_Permisos()
    override suspend fun GET_Usuarios(): Resource<ArrayList<User>> = repo.GET_Usuarios()
    override suspend fun PUT_Usuarios(
        uid: String,
        nombre: String,
        apellido: String,
        email: String,
        miRol: String,
        miFinca: String,
        miLinea: String,
        estado: Boolean
    ): Resource<String> = repo.PUT_Usuarios(uid, nombre, apellido, email, miRol, miFinca, miLinea, estado)


}