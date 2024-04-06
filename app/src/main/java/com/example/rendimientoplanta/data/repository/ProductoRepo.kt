package com.example.rendimientoplanta.data.repository
import com.example.rendimientoplanta.base.builder.BuilderObject
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.builder.TallosAsignadosBuilder
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.data.irepository.IProductoRepo
import com.example.rendimientoplanta.vo.Resource
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProductoRepo : IProductoRepo {
    private val TAG = "ProductoRepo"

    fun builderArrayList(builderObject: BuilderObject, querySnapshot: QuerySnapshot) : ArrayList<Any>{
        val array = ArrayList<Any>()
        for (change in querySnapshot.documentChanges) {
            if (change.type == DocumentChange.Type.ADDED) {
                MessageBuilder.sourceOrServer(querySnapshot, TAG)
                array.add(builderObject.documentSnapshotToObject(change.document))
            }
        }
        return array
    }

    override suspend fun GET_Stems(user: User): Resource<ArrayList<Int>> {
        val arrayValues = ArrayList<Int>()
        var total = 0
        var pendientes = 0
        val array = builderArrayList(TallosAsignadosBuilder(),
            FirebaseActions.getColletionWhereEqualTo("TallosAsignados", "fecha", SimpleDateFormat("yyyy-MM-dd").format(Date()), "lineaId", user.linea)) as ArrayList<TallosAsignados>
        return if (array.size!=0){
            for (tallo in array){
                total += tallo.tallos
                if(tallo.estado){
                    pendientes += tallo.tallos
                }
            }
            arrayValues.add(pendientes) // tallos pendientes
            arrayValues.add(total-pendientes) // tallos procesados
            Resource.Success(arrayValues)
        }else
            Resource.Failure(NullPointerException())
    }
}