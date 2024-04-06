package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.Finca
import com.google.firebase.firestore.DocumentSnapshot

class FincaBuilder : BuilderObject {

    override fun documentSnapshotToObject(document: DocumentSnapshot) =
        Finca(
            document.id.toInt(),
            document["nombre"].toString(),
            document["abreviatura"].toString(),
            document["estado"].toString().toBoolean(),
            document["sql"].toString().toBoolean()
        )
}
