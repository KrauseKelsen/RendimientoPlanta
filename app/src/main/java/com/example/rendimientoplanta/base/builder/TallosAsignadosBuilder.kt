package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.pojos.User
import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.util.*

class TallosAsignadosBuilder : BuilderObject {
    override fun documentSnapshotToObject(document: DocumentSnapshot) =
        TallosAsignados (
            document.id,
            document.get("fincaId").toString().toInt(),
            document.get("lineaId").toString().toInt(),
            document.get("fecha").toString(),
            document.get("horaInicio").toString(),
            document.get("horaFin").toString(),
            document.get("tallos").toString().toInt(),
            document.get("tipo").toString(),
            document.get("operarioId").toString().toInt(),
            document.get("operarioLineaId").toString(),
            document.get("nombreOperario").toString(),
            document.get("apellidosOperario").toString(),
            document.get("estado").toString().toBoolean(),
            document.get("fechaCreacion").toString(),
            document.get("user").toString(),
            document.get("primero").toString().toBoolean(),
            document.get("segundo").toString().toBoolean(),
            document.get("sql").toString().toBoolean()
        )

    fun buildTallosAsignados(
        operarioLinea: OperarioLinea,
        user: User,
        cantidad: Int,
        horaInicio: String,
        primero: Boolean,
        segundo: Boolean,
        tipoTalloSeleccionado: String
    ) =
        TallosAsignados(
            "${operarioLinea.fincaId}|${operarioLinea.lineaId}|${
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(
                    Date()
                )}|${operarioLinea.operarioId}|${operarioLinea.user}",
            operarioLinea.fincaId,
            operarioLinea.lineaId,
            SimpleDateFormat("yyyy-MM-dd").format(Date()),
            horaInicio,
            "--:--",
            cantidad,
            tipoTalloSeleccionado,
            operarioLinea.operarioId,
            operarioLinea.uid,
            operarioLinea.nombre,
            operarioLinea.apellidos,
            true,
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date()),
            user.uid,
            primero,
            segundo,
            false
        )
}