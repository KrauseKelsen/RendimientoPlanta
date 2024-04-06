package com.example.rendimientoplanta.data.irepository

import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.vo.Resource

interface ILogoutRepo {
    suspend fun PUT_Token(user: User, dispositivo: String): Resource<Boolean>

}