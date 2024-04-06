package com.example.rendimientoplanta.pattern.interprete

abstract class Expresion {
    fun interpretar24(contexto: Contexto){
        if (getHora(contexto.hora24).isEmpty())
            return

        if(getHora(contexto.hora24) == "00")
            construirHora(contexto, "12", getMinutes(contexto.hora24), zonaAM())

        if(getHora(contexto.hora24) == "01" || getHora(contexto.hora24) == "02"  || getHora(contexto.hora24) == "03"
            || getHora(contexto.hora24) == "04"  || getHora(contexto.hora24) == "05"  || getHora(contexto.hora24) == "06"  || getHora(contexto.hora24) == "07"
            || getHora(contexto.hora24) == "08"  || getHora(contexto.hora24) == "09"  || getHora(contexto.hora24) == "10"  || getHora(contexto.hora24) == "11")
            construirHora(contexto, getHora(contexto.hora24), getMinutes(contexto.hora24), zonaAM())

        if(getHora(contexto.hora24) == "12")
            construirHora(contexto, getHora(contexto.hora24), getMinutes(contexto.hora24), zonaPM())

        if(getHora(contexto.hora24) == "13")
            construirHora(contexto,"01", getMinutes(contexto.hora24), zonaPM())

        if(getHora(contexto.hora24) == "14")
            construirHora(contexto,"02", getMinutes(contexto.hora24), zonaPM())

        if(getHora(contexto.hora24) == "15")
            construirHora(contexto,"03", getMinutes(contexto.hora24), zonaPM())

        if(getHora(contexto.hora24) == "16")
            construirHora(contexto,"04", getMinutes(contexto.hora24), zonaPM())

        if(getHora(contexto.hora24) == "17")
            construirHora(contexto,"05", getMinutes(contexto.hora24), zonaPM())

        if(getHora(contexto.hora24) == "18")
            construirHora(contexto,"06", getMinutes(contexto.hora24), zonaPM())

        if(getHora(contexto.hora24) == "19")
            construirHora(contexto,"07", getMinutes(contexto.hora24), zonaPM())

        if(getHora(contexto.hora24) == "20")
            construirHora(contexto,"08", getMinutes(contexto.hora24), zonaPM())

        if(getHora(contexto.hora24) == "21")
            construirHora(contexto,"09", getMinutes(contexto.hora24), zonaPM())

        if(getHora(contexto.hora24) == "22")
            construirHora(contexto,"10", getMinutes(contexto.hora24), zonaPM())

        if(getHora(contexto.hora24) == "23")
            construirHora(contexto,"11", getMinutes(contexto.hora24), zonaPM())
    }

    fun interpretar12(contexto: Contexto){
        if((getHora(contexto.hora12).toInt() in 1..11) && getAMPM(contexto.hora12) == zonaAM())
            construirHora(contexto, getHora(contexto.hora12), getMinutes(contexto.hora12))

        if(getHora(contexto.hora12).toInt() == 12 && getAMPM(contexto.hora12) == zonaAM())
            construirHora(contexto, "${getHora(contexto.hora12).toInt()-12}", getMinutes(contexto.hora12))

        if(getHora(contexto.hora12).toInt() == 12 && getAMPM(contexto.hora12) == zonaPM())
            construirHora(contexto, getHora(contexto.hora12), getMinutes(contexto.hora12))

        if((getHora(contexto.hora12).toInt() in 1..11) && getAMPM(contexto.hora12) == zonaPM())
            construirHora(contexto, "${getHora(contexto.hora12).toInt()+12}", getMinutes(contexto.hora12))

    }

    abstract fun getMinutes(time: String) : String // recibe "12:00 A.M." -> devuelve "00"
    abstract fun getHora(time: String) : String // recibe "12:00 A.M." -> devuelve "12"
    abstract fun getAMPM(time: String) : String // recibe "12:00 A.M." A.M.
    abstract fun zonaAM() : String // A.M.
    abstract fun zonaPM() : String // P.M.
    abstract fun construirHora(contexto: Contexto, hora: String, minutos: String, ampm: String)
    abstract fun construirHora(contexto: Contexto, hora: String, minutos: String)
}