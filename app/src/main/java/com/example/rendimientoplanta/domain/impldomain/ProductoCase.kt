package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.data.irepository.IProductoRepo
import com.example.rendimientoplanta.domain.idomain.IProductoCase
import com.example.rendimientoplanta.vo.Resource

class ProductoCase(val repo: IProductoRepo): IProductoCase {
    override suspend fun GET_Stems(user: User): Resource<ArrayList<Int>> = repo.GET_Stems(user)
}