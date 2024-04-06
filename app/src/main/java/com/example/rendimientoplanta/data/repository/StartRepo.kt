package com.example.rendimientoplanta.data.repository

import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.builder.TokenBuilder
import com.example.rendimientoplanta.base.pojos.Rendimiento
import com.example.rendimientoplanta.base.pojos.Token
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions.GET_UserID
import com.example.rendimientoplanta.data.irepository.IStartRepo
import com.example.rendimientoplanta.vo.Resource
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.DocumentChange

class StartRepo : IStartRepo {
    private val TAG = "StartRepo"
    override suspend fun GET_Token(dispositivo: String): Resource<User> {
        //FIREBASE
        val querySnapshot = FirebaseActions.getColletionWhereEqualTo("Token", "dispositivo", dispositivo, "estado", true)
        for (change in querySnapshot.documentChanges) {
            if (change.type == DocumentChange.Type.ADDED) {
                MessageBuilder.sourceOrServer(querySnapshot, TAG)
                val token = TokenBuilder.tokenBuilder(change)
                return if (token.estado){
                    validarEstadoUsuario(token)
                }else
                    Resource.Failure(FirebaseAuthEmailException("USER","TOKEN"))
            }
        }
        return Resource.Failure(FirebaseException(""))
    }

    suspend fun validarEstadoUsuario(token: Token): Resource<User> {
        val user = GET_UserID(token.uid)
        return if (user.estado)
            Resource.Success(user)
        else
            Resource.Failure(FirebaseAuthInvalidCredentialsException("USER", "DISABLE"))
    }

    override suspend fun GET_Rendimiento(user: User): Resource<Rendimiento> {
        val rendimiento = FirebaseActions.GET_RendimientoID("Finca${user.fincaId}Linea${user.linea}")
        return Resource.Success(rendimiento)
    }
}