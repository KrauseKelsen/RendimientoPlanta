package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.data.irepository.IFincaRepo
import com.example.rendimientoplanta.domain.idomain.IFincaCase
import com.example.rendimientoplanta.vo.Resource

class FincaCase(val repo: IFincaRepo): IFincaCase {
    override suspend fun GET_Fincas(): Resource<ArrayList<Finca>> = repo.GET_Fincas()
    override suspend fun PUT_Finca(uid: Int, nombre: String, abreviatura: String, estado: Boolean, user: User):
            Resource<String> = repo.PUT_Finca(uid, nombre, abreviatura, estado, user)
}