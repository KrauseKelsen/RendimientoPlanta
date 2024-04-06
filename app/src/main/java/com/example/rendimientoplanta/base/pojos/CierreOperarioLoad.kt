package com.example.rendimientoplanta.base.pojos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CierreOperarioLoad (val uid: String,
                          val fincaId: Int,
                          val lineaId: Int,
                          val operarioId: Int,
                          val operario: String,
                          val fechaCierre: String,
                          val tallosParciales: Int,
                          val tallosCompletados: Int,
                          val tallosAsignados: Int,
                          val minutosEfectivos: Int,
                          val tallosXhora: Int,
                          val rendimientoXhora: Double,
                          val cierreLineaId: String,
                          val fechaCreacion: String,
                          val usuarioCreacion: String,
                          var usuarioCierre: String,
                          var cerrado: Boolean) : Parcelable