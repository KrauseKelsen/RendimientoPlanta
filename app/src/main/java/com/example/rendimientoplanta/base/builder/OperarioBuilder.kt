package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.Operario
import com.google.firebase.firestore.DocumentSnapshot

class OperarioBuilder: BuilderObject {
    override fun documentSnapshotToObject(document: DocumentSnapshot) =
        Operario(
            document.id.toInt(),
            document.get("identificacion").toString(),
            document.get("nombre").toString(),
            document.get("apellidos").toString(),
            if(document.get("codigo") == "null") 0 else document.get("codigo").toString().toInt(),
            document.get("estado").toString().toBoolean(),
            document.get("ocupado").toString().toBoolean(),
            document.get("usuarioCreacion").toString(),
            document.get("fechaCreacion").toString(),
            document.get("sql").toString().toBoolean()
        )
}