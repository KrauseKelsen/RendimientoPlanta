package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.Rendimiento
import com.google.firebase.firestore.DocumentSnapshot

class RendimientoBuilder: BuilderObject {
    override fun documentSnapshotToObject(document: DocumentSnapshot) =
        Rendimiento(
            document.id,
            if(document.get("fincaId") == "null") 0 else document.get("fincaId").toString().toInt(),
            if(document.get("linea") == "null") 0 else document.get("linea").toString().toInt(),
            if(document.get("rendimientoPorHora") == "null") 0 else document.get("rendimientoPorHora").toString().toInt(),
            document["fechaCreacion"].toString(),
            document["usuarioCreacion"].toString(),
            document["sql"].toString().toBoolean())
}