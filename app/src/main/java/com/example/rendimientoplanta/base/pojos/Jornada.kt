package com.example.rendimientoplanta.base.pojos

import java.text.SimpleDateFormat
import java.util.*

class Jornada{
    var uid = ""
    var fincaId = 0
    var lineaId = 0
    var fecha = ""
    var horaInicio = ""
    var horaFin = ""
    var fechaCreacion = ""
    var user = ""
    var sql = false

    constructor (puid: String, pfincaId: Int, plineaId: Int, pfecha: String, phoraInicio: String, phoraFin: String,
                 pfechaCreacion : String, puser : String, psql: Boolean){
        uid = puid
        fincaId = pfincaId
        lineaId = plineaId
        fecha = pfecha
        horaInicio = phoraInicio
        horaFin = phoraFin
        fechaCreacion = pfechaCreacion
        user = puser
        sql = psql
    }

    constructor(pfincaId: Int, plineaId: Int, phoraInicio: String, phoraFin: String, puser : String){
        fincaId = pfincaId
        lineaId = plineaId
        fecha = SimpleDateFormat("yyyy-MM-dd").format(Date())
        horaInicio = phoraInicio
        horaFin = phoraFin
        fechaCreacion = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        user = puser
    }

}
