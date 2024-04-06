package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.data.irepository.ILogoutRepo
import com.example.rendimientoplanta.domain.idomain.ILogoutCase
import com.example.rendimientoplanta.vo.Resource

class LogoutCase(val repo: ILogoutRepo): ILogoutCase {
    override suspend fun PUT_Token(user: User, dispositivo: String): Resource<Boolean> = repo.PUT_Token(user, dispositivo)
}