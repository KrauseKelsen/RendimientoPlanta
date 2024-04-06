package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.Motivo
import com.google.firebase.firestore.DocumentChange

object MotivoBuilder {

    fun motivoBuilder(change: DocumentChange) =
        Motivo(
            change.document.id,
            change.document["horaInicio"].toString(),
            change.document["horaFin"].toString(),
            change.document["motivo"].toString()
        )
}