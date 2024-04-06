package com.example.rendimientoplanta.base.builder

import android.util.Log
import com.google.firebase.firestore.QuerySnapshot

object MessageBuilder {

    var scloseApp = "cierre la aplicación e intente nuevamente, si el error persiste comuníquese con soporte"
    var sdisable = "lo sentimos, se ha deshabilitado"
    var sLoading = "Resource.Loading:"
    var sSuccess = "Resource.Success:"
    var sFailure = "Resource.Failure:"
    var sErrorLogin = "Error al iniciar sesión,"


    fun sourceOrServer(querySnapshot: QuerySnapshot, tag: String){
        if (querySnapshot.metadata.isFromCache) {
            Log.d(tag, "Información obtenida del cache en $tag")
        } else {
            Log.d(tag, "Información obtenida del servidor en $tag")
        }
    }
}