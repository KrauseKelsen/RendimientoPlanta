package com.example.rendimientoplanta.data.irepository

import com.example.rendimientoplanta.base.pojos.Linea
import com.example.rendimientoplanta.vo.Resource

interface ICierreLineaRepo {
    suspend fun GET_Lineas(): Resource<ArrayList<Linea>>
}