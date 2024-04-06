package com.example.rendimientoplanta.domain.idomain

import com.example.rendimientoplanta.base.pojos.Linea
import com.example.rendimientoplanta.vo.Resource

interface ICierreLineaCase {
    suspend fun GET_Lineas(): Resource<ArrayList<Linea>>
}