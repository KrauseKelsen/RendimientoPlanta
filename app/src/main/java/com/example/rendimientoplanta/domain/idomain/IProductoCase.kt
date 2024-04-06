package com.example.rendimientoplanta.domain.idomain

import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.vo.Resource

interface IProductoCase {
    suspend fun GET_Stems(user: User): Resource<ArrayList<Int>>
}