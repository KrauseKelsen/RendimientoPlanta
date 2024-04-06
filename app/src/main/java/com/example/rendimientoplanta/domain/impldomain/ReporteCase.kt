package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.model.ChartVertical
import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.data.irepository.IReporteRepo
import com.example.rendimientoplanta.domain.idomain.IReporteCase
import com.example.rendimientoplanta.vo.Resource

class ReporteCase(val repo: IReporteRepo): IReporteCase {
    override suspend fun GET_Lineas(fincaId: Int): Resource<ArrayList<Linea>> = repo.GET_Lineas(fincaId)
    override suspend fun GET_Operarios(fincaId: Int, lineaId: Int): Resource<ArrayList<OperarioLinea>> = repo.GET_Operarios(fincaId, lineaId)
    override suspend fun GET_ReportePorLinea(fincaId: Int, lineaId: Int): Resource<ChartVertical> = repo.GET_ReportePorLinea(fincaId, lineaId)
    override suspend fun GET_ReportePorOperario(fincaId: Int, lineaId: Int, operarioId: Int): Resource<ChartVertical> = repo.GET_ReportePorOperario(fincaId, lineaId, operarioId)

}