
package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.Linea
import com.google.firebase.firestore.DocumentSnapshot

class LineaBuilder: BuilderObject {
    override fun documentSnapshotToObject(document: DocumentSnapshot) =
        Linea(
            document.id.toInt(),
            document["nombre"].toString(),
            document["estado"].toString().toBoolean(),
            document["sql"].toString().toBoolean())
}