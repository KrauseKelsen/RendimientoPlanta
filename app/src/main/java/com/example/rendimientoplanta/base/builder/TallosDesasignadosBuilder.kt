package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.TallosDesasignados
import com.google.firebase.firestore.DocumentSnapshot

class TallosDesasignadosBuilder : BuilderObject {
    override fun documentSnapshotToObject(document: DocumentSnapshot) =
        TallosDesasignados (
            document.id,
            document.get("fincaId").toString().toInt(),
            document.get("lineaId").toString().toInt(),
            document.get("operarioId").toString().toInt(),
            document.get("tallosAsignados").toString().toInt(),
            document.get("tallosDesasignados").toString().toInt(),
            document.get("motivo").toString(),
            document.get("fechaCreacion").toString(),
            document.get("user").toString(),
            document.get("sql").toString().toBoolean()
        )
}