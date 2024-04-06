package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.pojos.Linea
import com.example.rendimientoplanta.data.irepository.ICierreLineaRepo
import com.example.rendimientoplanta.domain.idomain.ICierreLineaCase
import com.example.rendimientoplanta.vo.Resource

class CierreLineaCase(val repo: ICierreLineaRepo): ICierreLineaCase {
    override suspend fun GET_Lineas(): Resource<ArrayList<Linea>> = repo.GET_Lineas()
}