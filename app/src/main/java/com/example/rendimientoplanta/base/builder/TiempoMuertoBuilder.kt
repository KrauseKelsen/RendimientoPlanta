package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.Operario
import com.example.rendimientoplanta.base.pojos.TiempoMuerto
import com.example.rendimientoplanta.base.pojos.User
import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.util.*

class TiempoMuertoBuilder: BuilderObject {
    override fun documentSnapshotToObject(document: DocumentSnapshot) =
        TiempoMuerto(
            document.id,
            document.get("nombre").toString(),
            document.get("apellidos").toString(),
            document.get("operarioId").toString().toInt(),
            document.get("fincaId").toString().toInt(),
            document.get("fecha").toString(),
            document.get("horaInicio").toString(),
            document.get("horaFin").toString(),
            document.get("motivo").toString(),
            document.get("estado").toString().toBoolean(),
            document.get("fechaCreacion").toString(),
            document.get("user").toString(),
            document.get("sql").toString().toBoolean()
        )

    fun buildTiempoMuerto(operario: Operario, user: User, horaFin: String, horaInicio: String, motivo: String) = TiempoMuerto(
        "${user.fincaId}|${user.linea}|${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())}|${operario.codigo}|${user.uid}|${horaInicio}|${horaFin}",
        operario.nombre,
        operario.apellidos,
        operario.codigo,
        user.fincaId,
        SimpleDateFormat("yyyy-MM-dd").format(Date()),
        horaInicio,
        horaFin,
        motivo,
        true,
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date()),
        user.uid,
        false
    )
}