package com.example.rendimientoplanta.base.utils.instances

import android.app.Activity
import com.example.rendimientoplanta.base.pojos.Permiso
import com.example.rendimientoplanta.base.pojos.Rendimiento
import com.example.rendimientoplanta.base.pojos.User

object BundleActivity {
    fun updateBundle(activity: Activity, cantNotif : String) = activity.intent.putExtra("Nube", cantNotif)
    fun updateBundle(activity: Activity, user : User) = activity.intent.putExtra("User", user)
    fun updateBundle(activity: Activity, rendimiento: Rendimiento) = activity.intent.putExtra("Rendimiento", rendimiento)
    fun updateBundle(activity: Activity, permisos : Permiso) = activity.intent.putExtra("Permisos", permisos)
    fun updateBundle(activity: Activity) {
        activity.intent.putExtra("Nube", "0")
        activity.intent.putExtra("User", User("","","","","",0,"","","",false, false, 0))
        activity.intent.putExtra("Rendimiento", Rendimiento("",0,0,0,"","",false))
        activity.intent.putExtra("Permisos", Permiso("", arrayListOf(), false))
    }
}