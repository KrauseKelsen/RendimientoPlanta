package com.example.rendimientoplanta.pattern.interprete

class Contexto {
    var hora24 : String = "" // expresion
    var hora12 : String = "" // valor
    var AMPM : String = "" // valor

    fun setHoraDoce(hora: String, contexto: Contexto){
        contexto.hora12 = hora
        Reloj().interpretar12(contexto)
    }

    fun setHoraVeintiCuatro(hora: String, contexto: Contexto){
        contexto.hora24 = hora
        Reloj().interpretar24(contexto)
    }
}