package com.example.rendimientoplanta.data.repository

import com.example.rendimientoplanta.base.builder.*
import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.base.utils.instances.InstanceRetrofit
import com.example.rendimientoplanta.base.utils.instances.Network
import com.example.rendimientoplanta.data.irepository.IUsuarioRepo
import com.example.rendimientoplanta.data.service.UserService
import com.example.rendimientoplanta.vo.Resource
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import kotlin.collections.ArrayList

class UsuarioRepo : IUsuarioRepo {
    private val TAG = "UsuarioRepo"

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

    override suspend fun GET_Fincas(): Resource<ArrayList<Finca>> =
        Resource.Success(builderArrayList(FincaBuilder(),
            FirebaseActions.getColletionWhereEqualTo("Fincas", "estado", true)) as ArrayList<Finca>)

    override suspend fun GET_Lineas(): Resource<ArrayList<Linea>> =
        Resource.Success(builderArrayList(LineaBuilder(),
            FirebaseActions.getColletionWhereEqualTo("Lineas", "estado", true)) as ArrayList<Linea>)

    override suspend fun GET_Permisos(): Resource<ArrayList<Permiso>> =
        Resource.Success(builderArrayList(PermisoBuilder(),
            FirebaseActions.getColletion("Permisos")) as ArrayList<Permiso>)

    override suspend fun GET_Usuarios(): Resource<ArrayList<User>> =
        Resource.Success(builderArrayList(UserBuilder(),
            FirebaseActions.getColletion("User")) as ArrayList<User>)

    override suspend fun PUT_Usuarios(
        uid: String,
        nombre: String,
        apellido: String,
        email: String,
        miRol: String,
        miFinca: String,
        miLinea: String,
        estado: Boolean
    ): Resource<String> {
        var miFincaID = 0
        var abrev = ""
        (builderArrayList(FincaBuilder(),
            FirebaseActions.getColletionWhereEqualTo("Fincas", "estado", true)) as ArrayList<Finca>)
            .forEach { if (it.nombre == miFinca) {
                miFincaID = it.uid
                abrev = it.abreviatura
            } }
        var array = ArrayList<User>()

        array.add(
            User(uid, email,
                when (miRol) {
                    "Administrador" -> "Adm.1209"
                    "Administrador de planta" -> "AdmPlanta.1209"
                    "Guía" -> "Guia.1209"
                    "Alimentador" -> "Alimentador.1209"
                    else -> "UsuarioDesconocido.1209"
                },
            nombre, apellido, miFincaID, miFinca, abrev, miRol, estado,false, miLinea.substring(6).toInt())
        )
        if(Network.connectedTo()){
            val result = PR_UPD_USER(array)
            array.forEach { obj -> obj.sql = result}
        }else{
            array.forEach { obj -> obj.sql = false}
        }

        if(!array[0].estado){
            //se va a deshabilitar el usuario por lo tanto hay que bajar su token
            FirebaseActions.deleteDocument(array[0].uid, "Token")
        }
        FirebaseActions.createDocument(
            array[0].uid, "User",
            ObjectMapper().convertValue(array[0], object : TypeReference<Map<String, Any>>() {}))

        return Resource.Success(if(array[0].sql) "Usuario registrado exitosamente en el servidor." else "Se guardaron los datos exitosamente.")
    }

    fun PR_UPD_USER(jsonUsuarios: ArrayList<User>) = InstanceRetrofit.instanceRetrofit!!.create(
        UserService::class.java).upd_users(jsonUsuarios).execute().isSuccessful

}