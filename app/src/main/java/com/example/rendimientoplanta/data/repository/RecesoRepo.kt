package com.example.rendimientoplanta.data.repository

import com.example.rendimientoplanta.base.builder.RecesoBuilder
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.builder.TokenBuilder
import com.example.rendimientoplanta.base.pojos.Motivo
import com.example.rendimientoplanta.base.pojos.Receso
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.base.utils.instances.InstanceRetrofit
import com.example.rendimientoplanta.base.utils.instances.Network
import com.example.rendimientoplanta.data.irepository.IRecesoRepo
import com.example.rendimientoplanta.data.service.MotivoService
import com.example.rendimientoplanta.data.service.RecesoService
import com.example.rendimientoplanta.pattern.interprete.Contexto
import com.example.rendimientoplanta.vo.Resource
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.DocumentChange
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RecesoRepo : IRecesoRepo {
    private val TAG = "RecesoRepo"

    override suspend fun PUT_Receso(user: User, motivo: String, horaInicio: String, horaFin: String): Resource<Boolean> {
        val contextoInicio = Contexto()
        contextoInicio.setHoraDoce(horaInicio, contextoInicio)

        val contextoFin = Contexto()
        contextoFin.setHoraDoce(horaFin, contextoFin)

        val receso = Receso(user.fincaId, user.linea, contextoInicio.hora24, contextoFin.hora24, motivo, user.uid)
        receso.uid = "${receso.fincaId}|${receso.lineaId}|${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())}|${receso.user}"
        val array = ArrayList<Receso>()
        array.add(receso)
        receso.sql = if(Network.connectedTo()) PR_UPD_RECESO(array) else false
        FirebaseActions.createDocument(receso.uid, "Recesos", ObjectMapper()
            .convertValue(receso, object : TypeReference<Map<String, Any>>() {})).isSuccessful
        return Resource.Success(receso.sql)
    }

    fun PR_UPD_RECESO(jsonRecesos : ArrayList<Receso>) =  InstanceRetrofit.instanceRetrofit!!.create(RecesoService::class.java).upd_receso(jsonRecesos).execute().isSuccessful

    override suspend fun GET_Receso(dispositivo: String): Resource<ArrayList<Motivo>> {
        //FIREBASE
        val querySnapshot = FirebaseActions.getColletionWhereEqualTo("Token", "dispositivo", dispositivo, "estado", true)
        for (change in querySnapshot.documentChanges) {
            if (change.type == DocumentChange.Type.ADDED) {
                MessageBuilder.sourceOrServer(querySnapshot, TAG)
                val token = TokenBuilder.tokenBuilder(change)
                return if (token.estado){
                    val user = FirebaseActions.GET_UserID(token.uid)
                    if (user.estado)
                        GET_Receso(user) //tengo el usuario puedo pedir los motivos
                    else
                        Resource.Failure(FirebaseAuthInvalidCredentialsException("", "")) //usuario deshabilitado
                }else
                    Resource.Failure(FirebaseAuthEmailException("","")) //no se puedo traer el usuario

            }
        }

        return Resource.Failure(FirebaseException("")) // algo salio mal del tod0...
    }

    suspend fun GET_Receso(user: User): Resource<ArrayList<Motivo>> {
        val array = ArrayList<Receso>()
        val querySnapshot = FirebaseActions.
        getColletionWhereEqualTo("Recesos", "fecha", SimpleDateFormat("yyyy-MM-dd").format(Date()),
            "fincaId", user.fincaId, "lineaId", user.linea)
        for (change in querySnapshot.documentChanges) {
            if (change.type == DocumentChange.Type.ADDED) {
                MessageBuilder.sourceOrServer(querySnapshot, TAG)
                array.add(RecesoBuilder().documentSnapshotToObject(change.document))
            } else {
                return Resource.Failure(FirebaseAuthInvalidCredentialsException("1", "1"))
            }
        }
        return Resource.Success(buildMotivos(array))
    }

    fun buildMotivos(data: ArrayList<Receso>): ArrayList<Motivo>{
        val array = ArrayList<Motivo>()
        data.mapTo(array) { Motivo(it.uid, it.horaInicio, it.horaFin, it.motivo) }
        return array
    }

    override suspend fun DEL_Receso(motivo: Motivo): Resource<Boolean> {
        FirebaseActions.deleteDocument(motivo.uid, "Recesos")
        var res = false
        if (Network.connectedTo()) res = PR_DEL_RECESOS(arrayListOf(motivo.uid))
        if(!res) FirebaseActions.createDocument(motivo.uid, "Motivos", ObjectMapper()
            .convertValue(motivo, object : TypeReference<Map<String, Any>>() {}))
        return Resource.Success(res)
    }

    fun PR_DEL_RECESOS(documento: ArrayList<String>) =  InstanceRetrofit.instanceRetrofit!!.create(MotivoService::class.java).del_motivo(documento).execute().isSuccessful

}
