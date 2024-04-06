package com.example.rendimientoplanta.domain.idomain

import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.vo.Resource

interface IUsuarioCase {
    suspend fun GET_Fincas(): Resource<ArrayList<Finca>>
    suspend fun GET_Lineas(): Resource<ArrayList<Linea>>
    suspend fun GET_Permisos(): Resource<ArrayList<Permiso>>
    suspend fun GET_Usuarios(): Resource<ArrayList<User>>
    suspend fun PUT_Usuarios(
        uid: String,
        nombre: String,
        apellido: String,
        email: String,
        miRol: String,
        miFinca: String,
        miLinea: String,
        estado: Boolean
    ): Resource<String>
}