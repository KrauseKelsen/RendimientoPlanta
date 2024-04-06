package com.example.rendimientoplanta.data.service

import com.example.rendimientoplanta.base.pojos.TallosDesasignados
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*

interface TallosDesasignadosService {
    @POST("api/TallosDesasignados")
    fun upd_tallosDesasignados(@Body jsonTallosDesasignados: ArrayList<TallosDesasignados>): Call<Void>
}
