package com.example.rendimientoplanta.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*

interface DelPermisoService {
    @POST("api/DelPermiso")
    fun upd_delpermiso(@Body jsonPermisos: ArrayList<String>): Call<Void>
}
