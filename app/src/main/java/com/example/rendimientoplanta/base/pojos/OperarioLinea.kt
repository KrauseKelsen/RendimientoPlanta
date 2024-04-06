package com.example.rendimientoplanta.base.pojos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OperarioLinea(val uid: String, val nombre: String, val apellidos: String, val fincaId: Int,
                         val finca: String, val lineaId: Int, val fecha: String,
                         val horaInicio: String, var horaFin: String, val operarioId: Int,
                         var activo: Boolean, val fechaCreacion: String, val user: String,
                         var sql: Boolean) : Parcelable