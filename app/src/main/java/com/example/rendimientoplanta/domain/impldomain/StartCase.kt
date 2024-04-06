package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.pojos.Permiso
import com.example.rendimientoplanta.base.pojos.Rendimiento
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.data.irepository.IStartRepo
import com.example.rendimientoplanta.domain.idomain.IStartCase
import com.example.rendimientoplanta.vo.Resource

class StartCase(val repo: IStartRepo): IStartCase {
    override suspend fun GET_Token(dispositivo: String): Resource<User> = repo.GET_Token(dispositivo)
    override suspend fun GET_Rendimiento(user: User): Resource<Rendimiento> = repo.GET_Rendimiento(user)
}