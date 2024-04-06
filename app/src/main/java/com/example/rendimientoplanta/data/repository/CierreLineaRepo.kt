package com.example.rendimientoplanta.data.repository
import com.example.rendimientoplanta.base.builder.BuilderObject
import com.example.rendimientoplanta.base.builder.LineaBuilder
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.pojos.Linea
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.data.irepository.ICierreLineaRepo
import com.example.rendimientoplanta.vo.Resource
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import kotlin.collections.ArrayList

class CierreLineaRepo : ICierreLineaRepo {
    private val TAG = "CierreLineaRepo"

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

    override suspend fun GET_Lineas(): Resource<ArrayList<Linea>> {
        return Resource.Success(builderArrayList(LineaBuilder(), FirebaseActions.getColletionWhereEqualTo("Lineas", "estado", true)) as ArrayList<Linea>)
    }
}