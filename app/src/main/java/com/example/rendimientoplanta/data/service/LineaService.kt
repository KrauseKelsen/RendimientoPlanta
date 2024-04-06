package com.example.rendimientoplanta.data.service

import com.example.rendimientoplanta.base.pojos.Linea
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*

interface LineaService {
    @POST("api/Linea")
    fun upd_linea(@Body jsonLineas: ArrayList<Linea>): Call<Void>
}
