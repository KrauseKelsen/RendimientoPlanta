package com.example.rendimientoplanta.data.service

import com.example.rendimientoplanta.base.pojos.CierreOperario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*

interface CierreOperarioService {
    @POST("api/CierreOperario")
    fun upd_cierre_operario(@Body jsonCierreOperarios: ArrayList<CierreOperario>): Call<Void>
}
