package com.example.rendimientoplanta.data.service

import com.example.rendimientoplanta.base.pojos.OperarioLinea
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*

interface OperarioLineaService {
    @POST("api/OperarioLinea")
    fun upd_operariosLinea(@Body jsonOperariosLinea: ArrayList<OperarioLinea>): Call<Void>
}
