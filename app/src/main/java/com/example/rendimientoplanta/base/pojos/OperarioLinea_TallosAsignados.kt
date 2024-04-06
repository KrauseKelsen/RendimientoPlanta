package com.example.rendimientoplanta.base.pojos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OperarioLinea_TallosAsignados(var operarioLinea: OperarioLinea, var tallosAsignados: Boolean) : Parcelable