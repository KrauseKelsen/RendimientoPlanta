package com.example.rendimientoplanta.data.service

import com.example.rendimientoplanta.base.pojos.Finca
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*

interface FincaService {
    @POST("api/Finca")
    fun upd_finca(@Body jsonFincas: ArrayList<Finca>): Call<Void>
}
