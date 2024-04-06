package com.example.rendimientoplanta.data.service

import com.example.rendimientoplanta.base.pojos.Receso
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*

interface RecesoService {
    @POST("api/Receso")
    fun upd_receso(@Body jsonRecesos: ArrayList<Receso>): Call<Void>
}
