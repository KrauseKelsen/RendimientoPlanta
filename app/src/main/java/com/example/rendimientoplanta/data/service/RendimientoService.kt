package com.example.rendimientoplanta.data.service

import com.example.rendimientoplanta.base.pojos.Rendimiento
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*

interface RendimientoService {
    @POST("api/Rendimiento")
    fun upd_rendimiento(@Body jsonRendimiento: ArrayList<Rendimiento>): Call<Void>
}
