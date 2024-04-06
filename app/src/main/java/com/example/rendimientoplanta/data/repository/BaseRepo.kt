package com.example.rendimientoplanta.data.repository

import com.example.rendimientoplanta.base.builder.*
import com.example.rendimientoplanta.base.pojos.Seguridad
import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.base.utils.instances.InstanceRetrofit
import com.example.rendimientoplanta.base.utils.instances.Network
import com.example.rendimientoplanta.base.utils.validators.CalcuTimeValidators
import com.example.rendimientoplanta.data.irepository.IBaseRepo
import com.example.rendimientoplanta.data.service.RendimientoService
import com.example.rendimientoplanta.data.service.UserService
import com.example.rendimientoplanta.vo.Resource
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BaseRepo : IBaseRepo {
    private val TAG = "BaseRepo"

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

    override suspend fun GET_Permisos(rol: String): Resource<Permiso> {
        //FIREBASE
        return Resource.Success(FirebaseActions.GET_PermisoID(rol))
    }

    override suspend fun GET_Fincas(): Resource<ArrayList<Finca>> {
        return Resource.Success(builderArrayList(FincaBuilder(), FirebaseActions.getColletionWhereEqualTo("Fincas", "estado", true)) as ArrayList<Finca>)
    }

    override suspend fun GET_Lineas(): Resource<ArrayList<Linea>> {
        return Resource.Success(builderArrayList(LineaBuilder(), FirebaseActions.getColletionWhereEqualTo("Lineas", "estado", true)) as ArrayList<Linea>)
    }

    override suspend fun GET_Token(dispositivo: String): Resource<User> {
        val querySnapshot = FirebaseActions.getColletionWhereEqualTo("Token", "dispositivo", dispositivo, "estado", true)
        for (change in querySnapshot.documentChanges) {
            if (change.type == DocumentChange.Type.ADDED) {
                MessageBuilder.sourceOrServer(querySnapshot, TAG)
                val token = TokenBuilder.tokenBuilder(change)
                if (token.estado){
                    val user = FirebaseActions.GET_UserID(token.uid)
                    return if (user.estado)
                        Resource.Success(user)
                    else
                        Resource.Failure(FirebaseAuthInvalidCredentialsException("", ""))
                }else
                    Resource.Failure(FirebaseAuthEmailException("",""))

            }
        }
        return Resource.Failure(FirebaseException(""))
    }

    override suspend fun PUT_User(user: User, changePassword: Boolean, newPassword: String): Resource<Boolean> {
        val querySnapshot = FirebaseActions.getColletionWhereEqualTo("Fincas", "estado", true)
        for (change in querySnapshot.documentChanges) {
            if (change.type == DocumentChange.Type.ADDED) {
                MessageBuilder.sourceOrServer(querySnapshot, TAG)
                if(user.finca == FincaBuilder().documentSnapshotToObject(change.document).nombre){
                    user.fincaId = FincaBuilder().documentSnapshotToObject(change.document).uid
                    user.abrev = FincaBuilder().documentSnapshotToObject(change.document).abreviatura
                }
            }
        }

        if(changePassword){
            user.contrasenna = newPassword
        }

        val array = ArrayList<User>()
        array.add(user)
        user.sql = if (Network.connectedTo()) PR_UPD_USER(array) else false
        FirebaseActions.createDocument(user.uid, "User", ObjectMapper().convertValue(user, object : TypeReference<Map<String, Any>>() {})).isSuccessful

        return Resource.Success(user.sql)
    }

    fun PR_UPD_USER(jsonUsuarios: ArrayList<User>) = InstanceRetrofit.instanceRetrofit!!.create(UserService::class.java).upd_users(jsonUsuarios).execute().isSuccessful

    override suspend fun GET_Seguridad(modulo: String): Resource<ArrayList<Seguridad>> {
        val array = ArrayList<Seguridad>()
        val querySnapshot = FirebaseActions.getColletionWhereEqualTo("Seguridad", "estado", true, "modulo", modulo)
        for (change in querySnapshot.documentChanges) {
            if (change.type == DocumentChange.Type.ADDED) {
                MessageBuilder.sourceOrServer(querySnapshot, TAG)
                array.add(SeguridadBuilder().documentSnapshotToObject(change.document))
            }
        }
        return Resource.Success(array)
    }

    override suspend fun PUT_Rendimiento(user: User, valor: Int, rendimiento: Rendimiento, bandera: Boolean): Resource<Rendimiento> {
        if(rendimiento.linea == user.linea && rendimiento.fincaId == user.fincaId && valor != 0){
            rendimiento.rendimientoPorHora = valor
            rendimiento.linea = user.linea
            rendimiento.fincaId = user.fincaId
            rendimiento.fechaCreacion = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
            rendimiento.usuarioCreacion = user.uid
            rendimiento.uid = "Finca${user.fincaId}Linea${user.linea}"
        }

        if((rendimiento.linea != user.linea || rendimiento.fincaId != user.fincaId) && valor != 0){
            rendimiento.rendimientoPorHora = valor
            rendimiento.linea = user.linea
            rendimiento.fincaId = user.fincaId
            rendimiento.fechaCreacion = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
            rendimiento.usuarioCreacion = user.uid
            rendimiento.uid = "Finca${user.fincaId}Linea${user.linea}"
        }

        if(valor == 0 && bandera){
            rendimiento.rendimientoPorHora = valor
            rendimiento.linea = user.linea
            rendimiento.fincaId = user.fincaId
            rendimiento.fechaCreacion = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
            rendimiento.usuarioCreacion = user.uid
            rendimiento.uid = "Finca${user.fincaId}Linea${user.linea}"
        }

        val array = ArrayList<Rendimiento>()
        array.add(rendimiento)
        rendimiento.sql = if (Network.connectedTo()) PR_UPD_Rendimiento(array) else false
        FirebaseActions.createDocument(rendimiento.uid, "Rendimiento", ObjectMapper().convertValue(rendimiento, object : TypeReference<Map<String, Any>>() {})).isSuccessful


        return Resource.Success(FirebaseActions.GET_RendimientoID("Finca${user.fincaId}Linea${user.linea}"))
    }

    fun PR_UPD_Rendimiento(jsonRendimiento: ArrayList<Rendimiento>) =
        InstanceRetrofit.instanceRetrofit!!.create(RendimientoService::class.java).upd_rendimiento(jsonRendimiento).execute().isSuccessful

    override suspend fun GET_Rendimiento(user: User): Resource<Rendimiento> {
        val rendimiento = FirebaseActions.GET_RendimientoID("Finca${user.fincaId}Linea${user.linea}")
        return Resource.Success(rendimiento)
    }

    override suspend fun GET_RendimientoPorHora(operarioLinea: OperarioLinea, rendimiento: Rendimiento): Resource<String> {
        var recesos = builderArrayList(RecesoBuilder(), FirebaseActions.getColletionWhereEqualTo("Recesos",
            "fincaId", operarioLinea.fincaId,"lineaId", operarioLinea.lineaId,
            "fecha", SimpleDateFormat("yyyy-MM-dd").format(Date()))) as ArrayList<Receso>

        val tallosCerrados = builderArrayList(TallosAsignadosBuilder(), FirebaseActions.getColletionWhereEqualTo(
                "TallosAsignados", "fincaId", operarioLinea.fincaId, "operarioId", operarioLinea.operarioId,
                "lineaId", operarioLinea.lineaId, "fecha", operarioLinea.fecha,
            "estado", false)) as ArrayList<TallosAsignados>

        recesos = CalcuTimeValidators.getRecesosValidos(recesos, tallosCerrados)
        return if(tallosCerrados.size==0) Resource.Success(" 0") else
        Resource.Success("${CalcuTimeValidators.getTallosXHora(tallosCerrados, recesos, operarioLinea.operarioId)}")
    }
}