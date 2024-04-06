package com.example.rendimientoplanta.base.pojos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TiempoMuerto(val uid: String, val nombre: String, val apellidos: String,
                        val operarioId: Int, val fincaId: Int, val fecha: String,
                        val horaInicio: String, var horaFin: String, val motivo: String,
                        var estado: Boolean, val fechaCreacion: String, val user: String,
                        var sql: Boolean) : Parcelable