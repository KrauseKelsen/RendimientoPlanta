package com.example.rendimientoplanta.domain.idomain

import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.vo.Resource

interface ILogoutCase {
    suspend fun PUT_Token(user: User, dispositivo: String): Resource<Boolean>
}