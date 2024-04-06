
package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.CierreLinea
import com.example.rendimientoplanta.base.pojos.CierreLineaLoad
import com.example.rendimientoplanta.base.pojos.CierreOperario
import com.example.rendimientoplanta.base.pojos.CierreOperarioLoad
import com.google.firebase.firestore.DocumentSnapshot

class CierreLineaLoadBuilder: BuilderObject {
    override fun documentSnapshotToObject(document: DocumentSnapshot) =
        CierreLinea(
            document.id,
            document["fincaId"].toString().toInt(),
            document["lineaId"].toString().toInt(),
            document["fechaCierre"].toString(),
            document["horaInicio"].toString(),
            document["horaFin"].toString(),
            document["tallosParciales"].toString().toInt(),
            document["tallosCompletados"].toString().toInt(),
            document["minutosEfectivos"].toString().toInt(),
            document["tallosXhora"].toString().toInt(),
            document["rendimientoXhora"].toString().toDouble(),
            document["fechaCreacion"].toString(),
            document["usuarioCreacion"].toString(),
            document["usuarioCierre"].toString(),
            document["cerrado"].toString().toBoolean(),
            document["sql"].toString().toBoolean()
        )

    fun buildCierreLineaLoad(cierreLinea: CierreLinea, tallosAsignados: Int) =
        CierreLineaLoad(
            cierreLinea.uid,
            cierreLinea.fincaId,
            cierreLinea.lineaId,
            cierreLinea.fechaCierre,
            cierreLinea.horaInicio,
            cierreLinea.horaFin,
            cierreLinea.tallosParciales,
            cierreLinea.tallosCompletados,
            tallosAsignados,
            cierreLinea.minutosEfectivos,
            cierreLinea.tallosXhora,
            cierreLinea.rendimientoXhora,
            cierreLinea.fechaCreacion,
            cierreLinea.usuarioCreacion,
            cierreLinea.usuarioCierre,
            cierreLinea.cerrado,
            cierreLinea.sql,
            )
}