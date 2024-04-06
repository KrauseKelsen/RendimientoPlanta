package com.example.rendimientoplanta.data.repository

import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.builder.TallosAsignadosBuilder
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.pojos.TallosDesasignados
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.base.utils.instances.InstanceRetrofit
import com.example.rendimientoplanta.base.utils.instances.Network
import com.example.rendimientoplanta.base.utils.validators.CalcuTimeValidators
import com.example.rendimientoplanta.data.irepository.ISacarLineaRepo
import com.example.rendimientoplanta.data.service.OperarioLineaService
import com.example.rendimientoplanta.data.service.TallosAsignadosService
import com.example.rendimientoplanta.data.service.TallosDesasignadosService
import com.example.rendimientoplanta.pattern.interprete.Contexto
import com.example.rendimientoplanta.vo.Resource
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.firestore.DocumentChange
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SacarLineaRepo : ISacarLineaRepo {
    private val TAG = "SacarLineaRepo"
    override suspend fun PUT_SACAR_LINEA(operarioLinea: OperarioLinea, tallosAsignados: ArrayList<TallosAsignados>, cantidad: Int, motivo: String): Resource<Boolean> {
        val horaActual = SimpleDateFormat("HH:mm").format(Date())
        if(CalcuTimeValidators.AwhereGreaterThanToEqualB(tallosAsignados[0].horaInicio, horaActual)){
            val contexto = Contexto()
            contexto.setHoraVeintiCuatro(tallosAsignados[0].horaInicio, contexto)
            return Resource.Failure(Exception("No se pueden desasignar los tallos al operario seleccionado porque aún no ha iniciado " +
                    "su jornada ya que la misma empieza a las ${contexto.hora12} ${contexto.AMPM}"))
        }

        val tallosDesasignados = ArrayList<TallosDesasignados>()
        var operarioLineaNew = operarioLinea
        if(tallosAsignados.size==1){
            operarioLineaNew = UPD_OperarioLinea(operarioLinea)
        }

        val cantidadTallosAsignados = tallosAsignados[0].tallos
        val tallosAsignadosNew = UPD_TallosAsignados(tallosAsignados[0], cantidad)

        tallosDesasignados.add(
            TallosDesasignados(
                "${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())}|${operarioLineaNew.fincaId}|${operarioLineaNew.lineaId}|${operarioLineaNew.operarioId}|${operarioLineaNew.user}",
                operarioLineaNew.fincaId,
                operarioLineaNew.lineaId,
                operarioLineaNew.operarioId,
                cantidadTallosAsignados,
                cantidadTallosAsignados-tallosAsignadosNew.tallos,
                motivo,
                "${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())}",
                operarioLineaNew.user,
                false
            )
        )
        if(Network.connectedTo()){
            tallosDesasignados[0].sql = PR_UPD_TALLOS_DESASIGNADOS(tallosDesasignados)
        }else{
            tallosDesasignados[0].sql = false
        }


        if(tallosAsignados.size>1){
            tallosAsignados[1].horaInicio = tallosAsignadosNew.horaFin
            FirebaseActions.createDocument(tallosAsignados[1].uid, "TallosAsignados", ObjectMapper().convertValue(tallosAsignados[1], object : TypeReference<Map<String, Any>>() {}))
        }
        FirebaseActions.createDocument(tallosDesasignados[0].uid, "TallosDesasignados", ObjectMapper().convertValue(tallosDesasignados[0], object : TypeReference<Map<String, Any>>() {}))
        return Resource.Success(tallosDesasignados[0].sql && tallosAsignadosNew.sql && operarioLineaNew.sql)
    }

    fun PR_UPD_TALLOS_DESASIGNADOS(jsonTallosDesasignados : ArrayList<TallosDesasignados>) =
        InstanceRetrofit.instanceRetrofit!!.create(TallosDesasignadosService::class.java).upd_tallosDesasignados(jsonTallosDesasignados).execute().isSuccessful

    suspend fun UPD_OperarioLinea(operarioLinea: OperarioLinea): OperarioLinea {

        val arrayList = ArrayList<OperarioLinea>()
        operarioLinea.horaFin = SimpleDateFormat("HH:mm").format(Date())
        operarioLinea.activo = false
        arrayList.add(operarioLinea)
        if(Network.connectedTo()){
            operarioLinea.sql = PR_UPD_OPERARIOS_LINEA(arrayList)
        }else{
            operarioLinea.sql = false
        }
        FirebaseActions.createDocument(operarioLinea.uid, "OperariosEnLinea", ObjectMapper().convertValue(operarioLinea, object : TypeReference<Map<String, Any>>() {}))

        val operario = FirebaseActions.GET_OperarioID(operarioLinea.operarioId.toString())
        operario.ocupado = false
        FirebaseActions.createDocument(operario.uid.toString(), "Operarios", ObjectMapper().convertValue(operario, object : TypeReference<Map<String, Any>>() {}))
        return arrayList[0]
    }

    fun PR_UPD_OPERARIOS_LINEA(jsonOperariosLinea : ArrayList<OperarioLinea>) =
        InstanceRetrofit.instanceRetrofit!!.create(OperarioLineaService::class.java).upd_operariosLinea(jsonOperariosLinea).execute().isSuccessful

    suspend fun UPD_TallosAsignados(tallosAsignados: TallosAsignados, cantidad: Int): TallosAsignados {
        val arrayList = ArrayList<TallosAsignados>()
        val querySnapshot = FirebaseActions.getColletionWhereEqualTo("TallosAsignados", "operarioLineaId", tallosAsignados.operarioLineaId, "estado", true)
        for (change in querySnapshot.documentChanges) {
            if (change.type == DocumentChange.Type.ADDED) {
                MessageBuilder.sourceOrServer(querySnapshot, TAG)
                val tallosAsignados = TallosAsignadosBuilder().documentSnapshotToObject(change.document)
                tallosAsignados.horaFin = SimpleDateFormat("HH:mm").format(Date())
                tallosAsignados.estado = false
                tallosAsignados.tallos = cantidad
                arrayList.add(tallosAsignados)
            }
        }

        if(arrayList.size!=0){
            val tallosAsignados = arrayList[0]
            if(Network.connectedTo()){
                tallosAsignados.sql = PR_UPD_TALLOS_ASIGNADOS(arrayList)
            }else{
                tallosAsignados.sql = false
            }
            FirebaseActions.createDocument(tallosAsignados.uid, "TallosAsignados", ObjectMapper().convertValue(tallosAsignados, object : TypeReference<Map<String, Any>>() {}))
        }
        return arrayList[0]
    }

    fun PR_UPD_TALLOS_ASIGNADOS(jsonTallosAsignados: ArrayList<TallosAsignados>) =
        InstanceRetrofit.instanceRetrofit!!.create(TallosAsignadosService::class.java).upd_tallosAsignados(jsonTallosAsignados).execute().isSuccessful
}