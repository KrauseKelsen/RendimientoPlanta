package com.example.rendimientoplanta.data.service

import com.example.rendimientoplanta.base.pojos.TallosAsignados
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*

interface TallosAsignadosService {
    @POST("api/TallosAsignados")
    fun upd_tallosAsignados(@Body jsonTallosAsignados: ArrayList<TallosAsignados>): Call<Void>
}
