
package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.base.utils.validators.CalcuTimeValidators
import com.google.firebase.firestore.DocumentSnapshot
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CierreOperarioBuilder: BuilderObject {
    override fun documentSnapshotToObject(document: DocumentSnapshot) =
        CierreOperario(
            document.id,
            document["fincaId"].toString().toInt(),
            document["lineaId"].toString().toInt(),
            document["operarioId"].toString().toInt(),
            document["fechaCierre"].toString(),
            document["tallosParciales"].toString().toInt(),
            document["tallosCompletados"].toString().toInt(),
            document["minutosEfectivos"].toString().toInt(),
            document["tallosXhora"].toString().toInt(),
            document["rendimientoXhora"].toString().toDouble(),
            document["cierreLineaId"].toString(),
            document["fechaCreacion"].toString(),
            document["usuarioCreacion"].toString(),
            document["usuarioCierre"].toString(),
            document["cerrado"].toString().toBoolean(),
            document["sql"].toString().toBoolean()
        )

    fun buildCierreOperario(
        user: User,
        tallosParciales: Int,
        tallosAsignados: ArrayList<TallosAsignados>,
        operarioId: Int,
        cierreLinea: CierreLinea,
        recesos: ArrayList<Receso>,
        rendimiento: Rendimiento
    ) =
        CierreOperario(
            uid="${cierreLinea.uid}|$operarioId",
            fincaId=cierreLinea.fincaId,
            lineaId=cierreLinea.lineaId,
            operarioId=operarioId,
            fechaCierre=cierreLinea.fechaCierre,
            tallosParciales=tallosParciales,
            tallosCompletados=CalcuTimeValidators.getCantTallosAsignados(tallosAsignados, operarioId)-tallosParciales,
            minutosEfectivos=(CalcuTimeValidators.getMinutesTallos(tallosAsignados,operarioId) - CalcuTimeValidators.getMinutesRecesos(recesos)),
            tallosXhora=CalcuTimeValidators.getTallosXHora(tallosAsignados, recesos, operarioId).toInt(),

            rendimientoXhora=BigDecimal(((CalcuTimeValidators.getCantTallosAsignados(tallosAsignados , operarioId)
                .toDouble() / ((CalcuTimeValidators.getMinutesTallos(tallosAsignados, operarioId) -
                    CalcuTimeValidators.getMinutesRecesos(recesos)).toDouble() *
                    ((rendimiento.rendimientoPorHora.toDouble() / 60.0)))) * 100.0)).setScale(0, RoundingMode.HALF_EVEN).toDouble(),

            cierreLineaId=cierreLinea.uid,
            fechaCreacion=SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date()),
            usuarioCreacion=user.uid,
            usuarioCierre=user.uid,
            cerrado=true,
            sql=false
        )
}