package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.Receso
import com.google.firebase.firestore.DocumentSnapshot

class RecesoBuilder : BuilderObject{

    override fun documentSnapshotToObject(document: DocumentSnapshot) =
        Receso(
            document.id,
            document["fincaId"].toString().toInt(),
            document["lineaId"].toString().toInt(),
            document["fecha"].toString(),
            document["horaInicio"].toString(),
            document["horaFin"].toString(),
            document["motivo"].toString(),
            document["fechaCreacion"].toString(),
            document["user"].toString(),
            document["sql"].toString().toBoolean()
        )
}