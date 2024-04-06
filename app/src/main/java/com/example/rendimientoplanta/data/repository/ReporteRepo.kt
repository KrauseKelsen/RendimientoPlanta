package com.example.rendimientoplanta.data.repository

import com.example.rendimientoplanta.base.builder.*
import com.example.rendimientoplanta.base.model.ChartVertical
import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.base.utils.validators.CalcuTimeValidators
import com.example.rendimientoplanta.data.irepository.IReporteRepo
import com.example.rendimientoplanta.vo.Resource
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReporteRepo : IReporteRepo {
    private val TAG = "ReporteRepo"

    fun builderArrayList(builderObject: BuilderObject, querySnapshot: QuerySnapshot): ArrayList<Any> {
        val array = ArrayList<Any>()
        for (change in querySnapshot.documentChanges) {
            if (change.type == DocumentChange.Type.ADDED) {
                MessageBuilder.sourceOrServer(querySnapshot, TAG)
                array.add(builderObject.documentSnapshotToObject(change.document))
            }
        }
        return array
    }

    override suspend fun GET_Lineas(fincaId: Int): Resource<ArrayList<Linea>> =
        Resource.Success(builderArrayList(LineaBuilder(),
            FirebaseActions.getColletionWhereEqualTo("Lineas",
                "estado", true)) as ArrayList<Linea>)

    override suspend fun GET_Operarios(fincaId: Int, lineaId: Int): Resource<ArrayList<OperarioLinea>> =
        Resource.Success(builderArrayList(OperarioLineaBuilder(),
            FirebaseActions.getColletionWhereEqualTo("OperariosEnLinea", "fincaId", fincaId,
                "lineaId", lineaId)) as ArrayList<OperarioLinea>)

    override suspend fun GET_ReportePorLinea(fincaId: Int, lineaId: Int): Resource<ChartVertical> {
        val tallosChartVertical = ArrayList<Int>()

        val c  = GregorianCalendar.getInstance()
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        tallosChartVertical.add(getTallosHoy(df.format(c.time), fincaId, lineaId, 0))

        for (i in 1..6){
            c.add(Calendar.DATE, 1)
            tallosChartVertical.add(getTallosHoy(df.format(c.time), fincaId, lineaId, 0))
        }

        return Resource.Success(
            ChartVertical("Reporte por línea", CalcuTimeValidators.getArrayDays(), tallosChartVertical,
                CalcuTimeValidators.getArrayPorcentajes()))

    }

    override suspend fun GET_ReportePorOperario(fincaId: Int, lineaId: Int, operarioId: Int): Resource<ChartVertical> {
        val tallosChartVertical = ArrayList<Int>()

        val c  = GregorianCalendar.getInstance()
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        tallosChartVertical.add(getTallosHoy(df.format(c.time), fincaId, lineaId, operarioId))

        for (i in 1..6){
            c.add(Calendar.DATE, 1)
            tallosChartVertical.add(getTallosHoy(df.format(c.time), fincaId, lineaId, operarioId))
        }

        return Resource.Success(
            ChartVertical("Reporte por línea", CalcuTimeValidators.getArrayDays(), tallosChartVertical,
                CalcuTimeValidators.getArrayPorcentajes()))

    }

    suspend fun getTallosHoy(day: String, fincaId: Int, lineaId: Int, operarioId: Int): Int {
        var tallosHoy = 0
        val tallosAsignadosCerrados = builderArrayList(TallosAsignadosBuilder(),
            FirebaseActions.getColletionWhereEqualTo("TallosAsignados", "fincaId", fincaId,
                "lineaId", lineaId, "fecha", "$day", "estado", false))
                as ArrayList<TallosAsignados>

        if(operarioId==0){
            tallosAsignadosCerrados.forEach { tallosHoy += it.tallos}
        }else{
            tallosAsignadosCerrados.forEach { if(it.operarioId==operarioId) tallosHoy += it.tallos}
        }

        return tallosHoy
    }


}