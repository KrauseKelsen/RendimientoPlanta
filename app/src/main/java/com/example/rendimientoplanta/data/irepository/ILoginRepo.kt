package com.example.rendimientoplanta.data.irepository

import com.example.rendimientoplanta.base.pojos.Rendimiento
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.vo.Resource

interface ILoginRepo {
    suspend fun GET_loginRepo(email:String, contrasenna:String): Resource<User>
    suspend fun PUT_Token(user: User, dispositivo: String): Resource<Boolean>
    suspend fun GET_Rendimiento(user: User): Resource<Rendimiento>
}