package com.example.rendimientoplanta.domain.idomain

import com.example.rendimientoplanta.base.model.ChartVertical
import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.vo.Resource

interface IReporteCase {
    suspend fun GET_Lineas(fincaId: Int): Resource<ArrayList<Linea>>
    suspend fun GET_Operarios(fincaId: Int, lineaId: Int): Resource<ArrayList<OperarioLinea>>
    suspend fun GET_ReportePorLinea(fincaId: Int, lineaId: Int): Resource<ChartVertical>
    suspend fun GET_ReportePorOperario(fincaId: Int, lineaId: Int, operarioId: Int): Resource<ChartVertical>
}