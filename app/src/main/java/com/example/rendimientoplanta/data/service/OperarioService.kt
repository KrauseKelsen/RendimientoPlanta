package com.example.rendimientoplanta.data.service

import com.example.rendimientoplanta.base.pojos.Operario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import java.util.*

interface OperarioService {
    @POST("api/Operario")
    fun upd_operario(@Body jsonOperarios: ArrayList<Operario>): Call<Void>


    @GET("api/Operario")
    fun upd_operario(): Call<ArrayList<Operario>>
}
