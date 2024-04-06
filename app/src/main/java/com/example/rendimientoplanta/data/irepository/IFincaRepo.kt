package com.example.rendimientoplanta.data.irepository

import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.vo.Resource

interface IFincaRepo {
    suspend fun GET_Fincas(): Resource<ArrayList<Finca>>
    suspend fun PUT_Finca(uid: Int, nombre: String, abreviatura: String, estado: Boolean, user: User): Resource<String>
}