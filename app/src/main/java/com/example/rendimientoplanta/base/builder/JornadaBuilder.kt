package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.Jornada
import com.google.firebase.firestore.DocumentSnapshot

class JornadaBuilder : BuilderObject {
    override fun documentSnapshotToObject(document: DocumentSnapshot) =
        Jornada(
            document.id,
            document["fincaId"].toString().toInt(),
            document["lineaId"].toString().toInt(),
            document["fecha"].toString(),
            document["horaInicio"].toString(),
            document["horaFin"].toString(),
            document["fechaCreacion"].toString(),
            document["user"].toString(),
            document["sql"].toString().toBoolean()
        )
}