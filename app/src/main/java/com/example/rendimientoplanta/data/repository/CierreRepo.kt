package com.example.rendimientoplanta.data.repository
import com.example.rendimientoplanta.base.builder.*
import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.base.utils.instances.InstanceRetrofit
import com.example.rendimientoplanta.base.utils.instances.Network
import com.example.rendimientoplanta.base.utils.validators.CalcuTimeValidators
import com.example.rendimientoplanta.data.irepository.ICierreRepo
import com.example.rendimientoplanta.data.service.CierreLineaService
import com.example.rendimientoplanta.vo.Resource
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CierreRepo : ICierreRepo {
    private val TAG = "CierreRepo"

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

    override suspend fun SET_CierreLinea(user: User, horaInicio: String): Resource<CierreLinea> {
        val cierreLinea = CierreLinea(
            "${user.fincaId}|${user.linea}|${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())}|$horaInicio|${user.uid}",
            user.fincaId, user.linea, "${SimpleDateFormat("yyyy-MM-dd").format(Date())}", horaInicio, "--:--",
            0, 0, 0, 0, 0.0,
            "${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())}", user.uid, user.uid, false,false)

        val arrayList = ArrayList<CierreLinea>()
        arrayList.add(cierreLinea)

        if(Network.connectedTo()){
            val result = PR_UPD_CIERRE_LINEA(arrayList)
            arrayList.forEach { obj -> obj.sql = result}
        }else{
            arrayList.forEach { obj -> obj.sql = false}
        }

        arrayList.forEach { obj -> FirebaseActions.createDocument(obj.uid, "CierreLinea", ObjectMapper()
            .convertValue(obj, object : TypeReference<Map<String, Any>>() {})).isSuccessful}

        return Resource.Success(cierreLinea)
    }

    fun PR_UPD_CIERRE_LINEA(jsonCierreLinea: ArrayList<CierreLinea>): Boolean = InstanceRetrofit.instanceRetrofit!!.create(CierreLineaService::class.java).upd_cierre_linea(jsonCierreLinea).execute().isSuccessful

    override suspend fun GET_CierreLineaAbierto(user: User): Resource<ArrayList<CierreLinea>> {
        val cierresLinea = builderArrayList( CierreLineaBuilder(), FirebaseActions
            .getColletionWhereEqualTo("CierreLinea", "fincaId", user.fincaId,
                "lineaId", user.linea, "cerrado", false)) as ArrayList<CierreLinea>
        return Resource.Success(cierresLinea)
    }

    override suspend fun SET_CierreLinea(user: User,cierreLinea: CierreLinea, horaFin: String, rendimiento: Rendimiento): Resource<CierreLinea> {
        cierreLinea.horaFin = horaFin
        cierreLinea.usuarioCierre = user.uid
        cierreLinea.cerrado = true

        val arrayList = ArrayList<CierreLinea>()
        arrayList.add(cierreLinea)

        val cierresOperario = builderArrayList(CierreOperarioBuilder(), FirebaseActions
            .getColletionWhereEqualTo("CierreOperario", "cierreLineaId", cierreLinea.uid)) as ArrayList<CierreOperario>

        var sumMinutosEfectivosOperarios = 0
        for(cierreOperario in cierresOperario){
            cierreLinea.tallosParciales += cierreOperario.tallosParciales
            cierreLinea.tallosCompletados += cierreOperario.tallosCompletados
            sumMinutosEfectivosOperarios += cierreOperario.minutosEfectivos
        }
        cierreLinea.minutosEfectivos = sumMinutosEfectivosOperarios/cierresOperario.size
        cierreLinea.tallosXhora = CalcuTimeValidators.getTallosXHora(cierreLinea.tallosCompletados, cierreLinea.minutosEfectivos).toInt()
        cierreLinea.rendimientoXhora = CalcuTimeValidators
            .getRendimiento(cierreLinea.tallosCompletados, cierreLinea.minutosEfectivos, rendimiento)
        if(Network.connectedTo()){
            val result = PR_UPD_CIERRE_LINEA(arrayList)
            arrayList.forEach { obj -> obj.sql = result}
        }else{
            arrayList.forEach { obj -> obj.sql = false}
        }

        arrayList.forEach { obj -> FirebaseActions.createDocument(obj.uid, "CierreLinea", ObjectMapper()
            .convertValue(obj, object : TypeReference<Map<String, Any>>() {})).isSuccessful}
        return Resource.Success(cierreLinea)
    }

    override suspend fun GET_CierreLineaCerrado(user: User): Resource<ArrayList<CierreLineaLoad>> {
        var valorRetorno = ArrayList<CierreLineaLoad>()

        val cierresLineas = if (user.rol=="Administrador de planta" || user.rol=="Administrador")
            builderArrayList(CierreLineaBuilder(), FirebaseActions.getColletionWhereEqualTo("CierreLinea",
                "fincaId", user.fincaId, "fechaCierre", SimpleDateFormat("yyyy-MM-dd").format(Date()),
                "cerrado", true)) as ArrayList<CierreLinea>
        else
            builderArrayList(CierreLineaBuilder(), FirebaseActions.getColletionWhereEqualTo("CierreLinea", "fincaId", user.fincaId,
                "lineaId", user.linea, "fechaCierre", SimpleDateFormat("yyyy-MM-dd").format(Date()), "cerrado", true))
                    as ArrayList<CierreLinea>

        for(cierreLinea in cierresLineas){
            val tallosAsignadosCerradosBeforeHour = CalcuTimeValidators.getTallosCerradosBeforeHour(
                builderArrayList(TallosAsignadosBuilder(), FirebaseActions
                    .getColletionWhereEqualTo("TallosAsignados",
                        "fincaId", cierreLinea.fincaId,"lineaId", cierreLinea.lineaId,
                        "fecha", cierreLinea.fechaCierre, "estado", false))
                        as ArrayList<TallosAsignados>
                , cierreLinea.horaInicio)
            val tallosAsignadosAbiertosBeforeHour = CalcuTimeValidators.getTallosAbiertosBeforeHour(
                builderArrayList(TallosAsignadosBuilder(), FirebaseActions
                    .getColletionWhereEqualTo("TallosAsignados",
                        "fincaId", cierreLinea.fincaId,"lineaId", cierreLinea.lineaId,
                        "fecha", cierreLinea.fechaCierre, "estado", true))
                        as ArrayList<TallosAsignados>
                , cierreLinea.horaInicio)

            var tallosTotalesAsignados = 0
            tallosAsignadosCerradosBeforeHour.forEach { obj -> tallosTotalesAsignados += obj.tallos }
            tallosAsignadosAbiertosBeforeHour.forEach { obj -> tallosTotalesAsignados += obj.tallos }

            valorRetorno.add(CierreLineaLoadBuilder().buildCierreLineaLoad(cierreLinea, tallosTotalesAsignados))
        }

        return Resource.Success(valorRetorno)
    }
}