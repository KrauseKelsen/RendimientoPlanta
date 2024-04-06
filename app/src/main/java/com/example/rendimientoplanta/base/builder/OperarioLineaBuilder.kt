package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.Operario
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.User
import com.google.firebase.firestore.DocumentSnapshot

class OperarioLineaBuilder: BuilderObject {
    override fun documentSnapshotToObject(document: DocumentSnapshot) =
        OperarioLinea(
            document.id,
            document.get("nombre").toString(),
            document.get("apellidos").toString(),
            document.get("fincaId").toString().toInt(),
            if(document.get("finca").toString() == "null") "" else document.get("finca").toString(),
            document.get("lineaId").toString().toInt(),
            document.get("fecha").toString(),
            document.get("horaInicio").toString(),
            document.get("horaFin").toString(),
            document.get("operarioId").toString().toInt(),
            document.get("activo").toString().toBoolean(),
            document.get("fechaCreacion").toString(),
            document.get("user").toString(),
            document.get("sql").toString().toBoolean()
        )

    fun operarioLineaBuilder(operario: Operario, user: User, fecha: String, horaInicio: String, horaFin: String, activo: Boolean, fechaCreacion: String) =
        OperarioLinea(
            "${user.fincaId}|${user.linea}|$fechaCreacion|${operario.uid}|${user.uid}",
            operario.nombre,
            operario.apellidos,
            user.fincaId,
            if(user.finca == "null") "" else user.finca,
            user.linea,
            fecha,
            horaInicio,
            horaFin,
            operario.uid,
            activo,
            fechaCreacion,
            user.uid,
            false
        )

}