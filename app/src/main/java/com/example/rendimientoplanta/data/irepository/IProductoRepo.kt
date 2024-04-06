package com.example.rendimientoplanta.data.irepository

import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.vo.Resource

interface IProductoRepo {
    suspend fun GET_Stems(user: User): Resource<ArrayList<Int>>
}