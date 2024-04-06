package com.example.rendimientoplanta.data.repository
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.builder.UserBuilder
import com.example.rendimientoplanta.base.pojos.Rendimiento
import com.example.rendimientoplanta.base.pojos.Token
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.data.irepository.ILoginRepo
import com.example.rendimientoplanta.vo.Resource
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.DocumentChange

class LoginRepo : ILoginRepo {
    private val TAG = "LoginRepo"
    override suspend fun GET_loginRepo(email: String, contrasenna: String): Resource<User> {
        //FIREBASE
        val querySnapshot = FirebaseActions.getColletionWhereEqualTo("User", "email", email)
        for (change in querySnapshot.documentChanges) {
            if (change.type == DocumentChange.Type.ADDED) {
                MessageBuilder.sourceOrServer(querySnapshot, TAG)
                val user = UserBuilder().documentSnapshotToObject(change.document)
                return if (user.contrasenna == contrasenna) {
                    Resource.Success(user)
                } else {
                    Resource.Failure(FirebaseAuthInvalidCredentialsException("1", "1"))
                }
            }
        }
        return Resource.Failure(FirebaseException(""))
    }

    override suspend fun PUT_Token(user: User, dispositivo: String): Resource<Boolean> {
        return Resource.Success(FirebaseActions.createDocument(user.uid, "Token", ObjectMapper().convertValue(
            Token(user.uid, dispositivo, true), object : TypeReference<Map<String, Any>>() {})).isSuccessful)
    }

    override suspend fun GET_Rendimiento(user: User): Resource<Rendimiento> {
        val rendimiento = FirebaseActions.GET_RendimientoID("Finca${user.fincaId}Linea${user.linea}")
        return Resource.Success(rendimiento)
    }
}