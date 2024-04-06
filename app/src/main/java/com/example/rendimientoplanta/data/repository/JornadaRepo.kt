package com.example.rendimientoplanta.data.repository

import com.example.rendimientoplanta.base.builder.BuilderObject
import com.example.rendimientoplanta.base.builder.JornadaBuilder
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.builder.TallosAsignadosBuilder
import com.example.rendimientoplanta.base.pojos.Jornada
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.base.utils.instances.InstanceRetrofit
import com.example.rendimientoplanta.base.utils.instances.Network
import com.example.rendimientoplanta.base.utils.validators.CalcuTimeValidators
import com.example.rendimientoplanta.data.irepository.IJornadaRepo
import com.example.rendimientoplanta.data.service.JornadaService
import com.example.rendimientoplanta.data.service.TallosAsignadosService
import com.example.rendimientoplanta.pattern.interprete.Contexto
import com.example.rendimientoplanta.vo.Resource
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class JornadaRepo : IJornadaRepo {
    private val TAG = "JornadaRepo"

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

    override suspend fun PUT_Jornada(user: User, horaInicio: String, horaFin: String): Resource<Boolean> {
        if(horaFin=="--:-- -.-."){
            return Resource.Failure(IllegalArgumentException())
        }
        val arrayTallos = builderArrayList(TallosAsignadosBuilder(),
            FirebaseActions.
            getColletionWhereEqualTo("TallosAsignados", "fecha", SimpleDateFormat("yyyy-MM-dd").format(Date()),
                "fincaId", user.fincaId, "lineaId", user.linea, "primero", true)) as ArrayList<TallosAsignados>

        val contextoInicio = Contexto()
        contextoInicio.setHoraDoce(horaInicio, contextoInicio)

        val contextoFin = Contexto()
        contextoFin.setHoraDoce(horaFin, contextoFin)

        val jornada = Jornada(user.fincaId, user.linea, contextoInicio.hora24, contextoFin.hora24, user.uid)
        jornada.uid = "${jornada.fincaId}|${jornada.lineaId}|${jornada.fecha}"
        val array = ArrayList<Jornada>()
        array.add(jornada)
        jornada.sql = if(Network.connectedTo()) PR_UPD_JORNADA(array) else false
        FirebaseActions.createDocument(jornada.uid, "Jornadas", ObjectMapper()
            .convertValue(jornada, object : TypeReference<Map<String, Any>>() {})).isSuccessful

        if(arrayTallos.size!=0){
            for (tallo in arrayTallos){
                if(CalcuTimeValidators.AwhereGreaterThanToEqualB(jornada.horaInicio, tallo.horaInicio)){
                    tallo.horaInicio = jornada.horaInicio
                }
            }

            if(Network.connectedTo()){
                val result = PR_UPD_TALLOS_ASIGNADOS(arrayTallos)
                arrayTallos.forEach { tallo -> tallo.sql = result}
            }else{
                arrayTallos.forEach { tallo -> tallo.sql = false}
            }

            arrayTallos.forEach { tallo ->  FirebaseActions
                .createDocument(tallo.uid, "TallosAsignados", ObjectMapper()
                    .convertValue(tallo, object : TypeReference<Map<String, Any>>() {}))}
        }

        return Resource.Success(jornada.sql)
    }

    fun PR_UPD_JORNADA(jsonJornadas : ArrayList<Jornada>) =  InstanceRetrofit.instanceRetrofit!!.create(JornadaService::class.java).upd_jornada(jsonJornadas).execute().isSuccessful

    fun PR_UPD_TALLOS_ASIGNADOS(jsonTallosAsignados: ArrayList<TallosAsignados>) =
        InstanceRetrofit.instanceRetrofit!!.create(TallosAsignadosService::class.java).upd_tallosAsignados(jsonTallosAsignados).execute().isSuccessful

    override suspend fun GET_Jornada(user: User): Resource<ArrayList<Jornada>> {
        val querySnapshot = FirebaseActions.
        getColletionWhereEqualTo("Jornadas", "fecha", SimpleDateFormat("yyyy-MM-dd").format(Date()),
            "fincaId", user.fincaId, "lineaId", user.linea)
        return Resource.Success(builderArrayList(JornadaBuilder(), querySnapshot) as ArrayList<Jornada>)
    }
}
