package com.example.rendimientoplanta.base.pojos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TallosAsignados (val uid: String, val fincaId: Int, val lineaId: Int, val fecha: String,
                       var horaInicio: String, var horaFin: String, var tallos: Int, var tipo: String, var operarioId : Int,
                       var operarioLineaId: String, var nombreOperario: String, var apellidosOperario: String,
                       var estado: Boolean, var fechaCreacion: String, var user: String,
                       var primero: Boolean, var segundo: Boolean, var sql: Boolean) : Parcelable