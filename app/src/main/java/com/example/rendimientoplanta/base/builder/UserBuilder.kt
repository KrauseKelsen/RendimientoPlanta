package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.User
import com.google.firebase.firestore.DocumentSnapshot

class UserBuilder: BuilderObject {
    override fun documentSnapshotToObject(document: DocumentSnapshot) =
        User(
            document.id,
            document["email"].toString(),
            document["contrasenna"].toString(),
            document["nombre"].toString(),
            document["apellidos"].toString(),
            document["fincaId"].toString().toInt(),
            document["finca"].toString(),
            document["abrev"].toString(),
            document["rol"].toString(),
            document["estado"].toString().toBoolean(),
            document["sql"].toString().toBoolean(),
            document["linea"].toString().toInt())
}