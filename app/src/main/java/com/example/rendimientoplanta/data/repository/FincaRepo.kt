package com.example.rendimientoplanta.data.repository

import com.example.rendimientoplanta.base.builder.*
import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.base.utils.instances.InstanceRetrofit
import com.example.rendimientoplanta.base.utils.instances.Network
import com.example.rendimientoplanta.data.irepository.IFincaRepo
import com.example.rendimientoplanta.data.service.*
import com.example.rendimientoplanta.vo.Resource
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import kotlin.collections.ArrayList

class FincaRepo : IFincaRepo {
    private val TAG = "FincaRepo"

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
            FirebaseActions.getColletion("Fincas")) as ArrayList<Finca>)



    override suspend fun PUT_Finca(uid: Int, nombre: String, abreviatura: String, estado: Boolean, user: User): Resource<String> {
        var array = ArrayList<Finca>()

        array.add(Finca(uid, nombre, abreviatura, estado, false))
        if(Network.connectedTo()){
            val result = PR_UPD_FINCAS(array)
            array.forEach { obj -> obj.sql = result}
        }else{
            array.forEach { obj -> obj.sql = false}
        }

        FirebaseActions.createDocument(array[0].uid.toString(), "Fincas",
            ObjectMapper().convertValue(array[0], object : TypeReference<Map<String, Any>>() {}))

        return Resource.Success(if(array[0].sql) "Finca actualizada exitosamente en el servidor." else "Se guardaron los datos exitosamente.")
    }

    fun PR_UPD_FINCAS(jsonFincas: ArrayList<Finca>) =
        InstanceRetrofit.instanceRetrofit!!.create(FincaService::class.java).upd_finca(jsonFincas).execute().isSuccessful

}