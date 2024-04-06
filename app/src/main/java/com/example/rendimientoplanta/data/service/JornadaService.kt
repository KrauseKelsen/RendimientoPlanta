package com.example.rendimientoplanta.data.service

import com.example.rendimientoplanta.base.pojos.Jornada
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*

interface JornadaService {
    @POST("api/Jornada")
    fun upd_jornada(@Body jsonJornadas: ArrayList<Jornada>): Call<Void>
}
