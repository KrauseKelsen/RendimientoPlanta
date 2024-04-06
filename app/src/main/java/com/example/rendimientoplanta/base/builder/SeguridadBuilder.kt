
package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.Seguridad
import com.google.firebase.firestore.DocumentSnapshot

class SeguridadBuilder: BuilderObject {
    override fun documentSnapshotToObject(document: DocumentSnapshot) =
        Seguridad(
            document.id,
            document["contrasenna"].toString(),
            document["modulo"].toString(),
            document["estado"].toString().toBoolean()
        )
}