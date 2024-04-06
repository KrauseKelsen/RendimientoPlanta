package com.example.rendimientoplanta.data.service

import com.example.rendimientoplanta.base.pojos.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*

interface UserService {
    @POST("api/Usuario")
    fun upd_users(@Body jsonUsuarios: ArrayList<User>): Call<Void>
}
