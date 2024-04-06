package com.example.rendimientoplanta.base.utils.instances

import android.annotation.SuppressLint
import com.example.rendimientoplanta.base.builder.*
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await


object FirebaseActions{

    @SuppressLint("StaticFieldLeak")
    var firebaseFirestore = FirebaseFirestore.getInstance()

    init{
        firebaseFirestore.firestoreSettings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
    }

    suspend fun getColletion(collection: String) = firebaseFirestore.collection(collection).get(if (Network.connectedToInternet()) Source.SERVER else Source.CACHE).await()
    suspend fun getColletionWhereEqualTo(collection: String, where: String, valueWhere: Any) = firebaseFirestore.collection(collection)
        .whereEqualTo(where, valueWhere).whereEqualTo(where, valueWhere).get(if (Network.connectedToInternet()) Source.SERVER else Source.CACHE).await()
    suspend fun getColletionWhereEqualTo(collection: String, where1: String, valueWhere1: Any, where2: String, valueWhere2: Any) = firebaseFirestore.collection(collection)
        .whereEqualTo(where1, valueWhere1).whereEqualTo(where2, valueWhere2).get(if (Network.connectedToInternet()) Source.SERVER else Source.CACHE).await()
    suspend fun getColletionWhereEqualTo(collection: String, where1: String, valueWhere1: Any, where2: String, valueWhere2: Any, where3: String, valueWhere3: Any) =
        firebaseFirestore.collection(collection)
            .whereEqualTo(where1, valueWhere1).whereEqualTo(where2, valueWhere2).whereEqualTo(where3, valueWhere3)
        .get(if (Network.connectedToInternet()) Source.SERVER else Source.CACHE).await()
    suspend fun getColletionWhereEqualTo(collection: String, where1: String, valueWhere1: Any, where2: String,
        valueWhere2: Any, where3: String, valueWhere3: Any, where4: String, valueWhere4: Any) =
        firebaseFirestore.collection(collection)
            .whereEqualTo(where1, valueWhere1).whereEqualTo(where2, valueWhere2).whereEqualTo(where3, valueWhere3).whereEqualTo(where4, valueWhere4)
        .get(if (Network.connectedToInternet()) Source.SERVER else Source.CACHE).await()
    suspend fun getColletionWhereEqualTo(collection: String, where1: String, valueWhere1: Any, where2: String,
        valueWhere2: Any, where3: String, valueWhere3: Any, where4: String, valueWhere4: Any, where5: String, valueWhere5: Any) =
        firebaseFirestore.collection(collection)
            .whereEqualTo(where1, valueWhere1).whereEqualTo(where2, valueWhere2)
            .whereEqualTo(where3, valueWhere3).whereEqualTo(where4, valueWhere4).whereEqualTo(where5, valueWhere5)
        .get(if (Network.connectedToInternet()) Source.SERVER else Source.CACHE).await()

    fun getReferenceID(collection: String, documentId: String) = firebaseFirestore.collection(collection).document(documentId)

    suspend fun getCollectionSQL(collection: String) = firebaseFirestore.collection(collection)
        .whereEqualTo("sql", false).get(if (Network.connectedToInternet()) Source.SERVER else Source.CACHE).await()

    suspend fun getAllCollectionSQL(collection: String) = firebaseFirestore.collection(collection).get(if (Network.connectedToInternet()) Source.SERVER else Source.CACHE).await()

    fun createDocument(doc: String, collection: String, data: Map<String, Any>) = firebaseFirestore.collection(collection).document(doc).set(data)

    fun deleteDocument(doc: String, collection: String) = firebaseFirestore.collection(collection).document(doc).delete()

    suspend fun GET_PermisoID(uid: String) = PermisoBuilder().documentSnapshotToObject(getReferenceID("Permisos", uid)
        .get(if (Network.connectedToInternet()) Source.SERVER else Source.CACHE).await())

    suspend fun GET_UserID(uid: String) = UserBuilder().documentSnapshotToObject(getReferenceID("User", uid)
        .get(if (Network.connectedToInternet()) Source.SERVER else Source.CACHE).await())

    suspend fun GET_OperarioID(codigo: String) = OperarioBuilder().documentSnapshotToObject(getReferenceID("Operarios", codigo)
        .get(if (Network.connectedToInternet()) Source.SERVER else Source.CACHE).await())

    suspend fun GET_CierreOperarioID(codigo: String) = CierreOperarioBuilder().documentSnapshotToObject(getReferenceID("CierreOperario", codigo)
        .get(if (Network.connectedToInternet()) Source.SERVER else Source.CACHE).await())

    suspend fun GET_RendimientoID(codigo: String) = RendimientoBuilder().documentSnapshotToObject(getReferenceID("Rendimiento", codigo)
        .get(if (Network.connectedToInternet()) Source.SERVER else Source.CACHE).await())

    suspend fun GET_FincaID(codigo: String) = FincaBuilder().documentSnapshotToObject(getReferenceID("Fincas", codigo)
        .get(if (Network.connectedToInternet()) Source.SERVER else Source.CACHE).await())
}