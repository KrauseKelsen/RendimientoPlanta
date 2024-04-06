package com.example.rendimientoplanta.data.irepository

import com.example.rendimientoplanta.base.pojos.Rendimiento
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.vo.Resource

interface IStartRepo {
    suspend fun GET_Token(dispositivo: String): Resource<User>
    suspend fun GET_Rendimiento(user: User): Resource<Rendimiento>
}