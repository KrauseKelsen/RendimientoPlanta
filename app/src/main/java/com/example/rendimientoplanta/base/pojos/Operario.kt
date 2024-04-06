package com.example.rendimientoplanta.base.pojos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
    data class Operario(
    var uid: Int, val identificacion: String, val nombre: String, val apellidos: String,
    var codigo: Int, var estado: Boolean, var ocupado: Boolean, var usuarioCreacion: String,
    val fechaCreacion: String, var sql: Boolean) : Parcelable