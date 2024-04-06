package com.example.rendimientoplanta.base.pojos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Permiso(val uid: String, val nombres: ArrayList<String>, var sql: Boolean)  : Parcelable