package com.example.rendimientoplanta.data.repository
import com.example.rendimientoplanta.base.pojos.Token
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.data.irepository.ILogoutRepo
import com.example.rendimientoplanta.vo.Resource
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

class LogoutRepo : ILogoutRepo {
    private val TAG = "LogoutRepo"

    override suspend fun PUT_Token(user: User, dispositivo: String): Resource<Boolean> {
        return Resource.Success(FirebaseActions.createDocument(user.uid, "Token", ObjectMapper()
            .convertValue(Token(user.uid, dispositivo, false), object : TypeReference<Map<String, Any>>() {})).isSuccessful)
    }
}