package com.example.rendimientoplanta.base.pojos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CierreLineaLoad (val uid: String,
                       val fincaId: Int,
                       val lineaId: Int,
                       val fechaCierre: String,
                       val horaInicio: String,
                       var horaFin: String,
                       var tallosParciales: Int,
                       var tallosCompletados: Int,
                       var tallosAsignados: Int,
                       var minutosEfectivos: Int,
                       var tallosXhora: Int,
                       var rendimientoXhora: Double,
                       val fechaCreacion: String,
                       val usuarioCreacion: String,
                       var usuarioCierre: String,
                       var cerrado: Boolean,
                       var sql: Boolean) : Parcelable