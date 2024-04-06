package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.domain.idomain.IReporteCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers


class ReporteVM (iReporteCase: IReporteCase): ViewModel() {
    private var piReporteCase = iReporteCase

    fun getLineas(fincaId: Int) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piReporteCase.GET_Lineas(fincaId))
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun getOperarios(fincaId: Int, lineaId: Int) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piReporteCase.GET_Operarios(fincaId, lineaId))
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun getReportePorLinea(fincaId: Int, lineaId: Int) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piReporteCase.GET_ReportePorLinea(fincaId, lineaId))
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun getReportePorOperario(fincaId: Int, lineaId: Int, operarioId: Int) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piReporteCase.GET_ReportePorOperario(fincaId, lineaId, operarioId))
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }
}