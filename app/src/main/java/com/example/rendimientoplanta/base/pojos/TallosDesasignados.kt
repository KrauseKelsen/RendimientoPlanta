package com.example.rendimientoplanta.base.pojos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TallosDesasignados (val uid: String, val fincaId: Int, val lineaId: Int, var operarioId : Int,
                          var tallosAsignados: Int, var tallosDesasignados: Int, val motivo: String,
                          var fechaCreacion: String, var user: String, var sql: Boolean) : Parcelable
