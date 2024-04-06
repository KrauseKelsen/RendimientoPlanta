package com.example.rendimientoplanta.data.service

import com.example.rendimientoplanta.base.pojos.CierreLinea
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*

interface CierreLineaService {
    @POST("api/CierreLinea")
    fun upd_cierre_linea(@Body jsonCierreLineas: ArrayList<CierreLinea>): Call<Void>
}
