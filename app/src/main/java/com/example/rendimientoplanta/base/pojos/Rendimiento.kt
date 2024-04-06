package com.example.rendimientoplanta.base.pojos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Rendimiento(
    var uid: String, var fincaId: Int, var linea: Int, var rendimientoPorHora: Int,
    var fechaCreacion: String, var usuarioCreacion: String, var sql: Boolean) : Parcelable