package com.example.rendimientoplanta.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*

interface MotivoService {
    @POST("api/Motivo")
    fun del_motivo(@Body jsonMotivos: ArrayList<String>): Call<Void>
}
