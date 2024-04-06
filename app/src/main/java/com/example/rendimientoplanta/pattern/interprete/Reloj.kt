package com.example.rendimientoplanta.pattern.interprete

class Reloj: Expresion() {
    override fun getMinutes(time: String): String {
        val min = Integer.parseInt(time.split(" ")[0].split(":")[1])
        return if(min<10) "0$min" else min.toString()
    }

    override fun getHora(time: String): String {
        val hora = Integer.parseInt(time.split(" ")[0].split(":")[0])
        return if(hora<10) "0$hora" else hora.toString()
    }

    override fun getAMPM(time: String): String {
        return time.split(" ")[1]
    }

    override fun zonaAM(): String {
        return "a.m."
    }
    override fun zonaPM(): String {
        return "p.m."
    }

    override fun construirHora(contexto: Contexto, hora: String, minutos: String, ampm: String){
        contexto.hora12 = "${hora}:${minutos}"
        contexto.AMPM = ampm
    }

    override fun construirHora(contexto: Contexto, hora: String, minutos: String){
        contexto.hora24 = "${hora}:${minutos}"
        contexto.AMPM = "-.-."
    }
}