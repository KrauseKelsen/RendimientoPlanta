package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.pojos.Rendimiento
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.data.irepository.ILoginRepo
import com.example.rendimientoplanta.domain.idomain.ILoginCase
import com.example.rendimientoplanta.vo.Resource

class LoginCase(val repo: ILoginRepo): ILoginCase{
    override suspend fun GET_loginRepo(email:String, contrasenna:String): Resource<User> = repo.GET_loginRepo(email, contrasenna)
    override suspend fun PUT_Token(user: User, dispositivo: String): Resource<Boolean> = repo.PUT_Token(user, dispositivo)
    override suspend fun GET_Rendimiento(user: User): Resource<Rendimiento> = repo.GET_Rendimiento(user)
}