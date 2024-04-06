
package com.example.rendimientoplanta.base.builder

import com.example.rendimientoplanta.base.pojos.*

class CierreOperarioLoadBuilder: BuilderObject {

    fun buildCierreOperarioLoad(cierreOperario: CierreOperario, nombreOperario: String, tallosAsignados: Int) =
        CierreOperarioLoad(
            cierreOperario.uid,
            cierreOperario.fincaId,
            cierreOperario.lineaId,
            cierreOperario.operarioId,
            nombreOperario,
            cierreOperario.fechaCierre,
            cierreOperario.tallosParciales,
            cierreOperario.tallosCompletados,
            tallosAsignados,
            cierreOperario.minutosEfectivos,
            cierreOperario.tallosXhora,
            cierreOperario.rendimientoXhora,
            cierreOperario.cierreLineaId,
            cierreOperario.fechaCreacion,
            cierreOperario.usuarioCreacion,
            cierreOperario.usuarioCierre,
            cierreOperario.cerrado
        )
}