package com.example.rendimientoplanta.data.service

import com.example.rendimientoplanta.base.pojos.Permiso
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*

interface PermisoService {
    @POST("api/Permiso")
    fun upd_permiso(@Body jsonPermisos: ArrayList<Permiso>): Call<Void>
}
