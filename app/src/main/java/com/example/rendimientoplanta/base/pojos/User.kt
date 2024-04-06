package com.example.rendimientoplanta.base.pojos
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(val uid: String, val email:String, var contrasenna:String, val nombre: String,
                val apellidos: String, var fincaId: Int, var finca: String, var abrev: String, val rol: String,
                var estado: Boolean, var sql: Boolean, var linea: Int) : Parcelable