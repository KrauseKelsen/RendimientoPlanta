package com.example.rendimientoplanta.base.utils.validators

import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.pattern.interprete.Contexto
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import java.math.BigDecimal
import java.math.RoundingMode

object CalcuTimeValidators {
    private var contextoInicio = Contexto()
    private var contextoFin = Contexto()
    val TAG = "CalcuTimeValidators"
    fun AwhereGreaterThanB(a: String, b: String) =
        (Integer.parseInt("${a.split(":")[0]}${a.split(":")[1]}") >
                Integer.parseInt("${b.split(":")[0]}${b.split(":")[1]}"))
    fun AwhereGreaterThanToEqualB(a: String, b: String) =
        (Integer.parseInt("${a.split(":")[0]}${a.split(":")[1]}") >=
                Integer.parseInt("${b.split(":")[0]}${b.split(":")[1]}"))
    fun getMinuteDifference(a: String, b: String): Int {
        val horas = Integer.parseInt(a.split(":")[0]) - Integer.parseInt(b.split(":")[0])
        val minutos = Integer.parseInt(a.split(":")[1]) - Integer.parseInt(b.split(":")[1])
        val horasAminutos = horas * 60
        val totalEnMinutos = horasAminutos + minutos
        return totalEnMinutos
    }

    fun getHourMoreOneMinute(a: String): String = getHourIntTogetHourString((Integer.parseInt("${a.split(":")[0]}${a.split(":")[1]}")+1))

    fun getHourIntTogetHourString(hourInt : Int): String{
        var horas = ""
        var minutos = ""
        horas = if(hourInt.toString().length < 4){
            minutos = "${hourInt.toString().substring(1)}"
            if("${hourInt.toString().substring(0,1)}".toInt() < 10){
                "0${hourInt.toString().substring(0,1)}"
            }else{
                "${hourInt.toString().substring(0,1)}"
            }
        }else{
            minutos = "${hourInt.toString().substring(2)}"
            if("${hourInt.toString().substring(0, 2)}".toInt() < 10){
                "0${hourInt.toString().substring(0, 2)}"
            }else{
                "${hourInt.toString().substring(0, 2)}"
            }
        }

        return "$horas:$minutos"
    }

    fun getTallosBeforeHourOfOperario(tallosAsignados: ArrayList<TallosAsignados>, hora: String, operarioId: Int) : ArrayList<TallosAsignados> {
        val array = ArrayList<TallosAsignados>()
        tallosAsignados.forEach { tallo -> if (AwhereGreaterThanToEqualB(hora, tallo.horaFin) && tallo.operarioId == operarioId) array.add(tallo) }
        return array
    }

    fun getTallosCerradosBeforeHour(tallosAsignados: ArrayList<TallosAsignados>, hora: String) : ArrayList<TallosAsignados> {
        val array = ArrayList<TallosAsignados>()
        tallosAsignados.forEach { tallo -> if (AwhereGreaterThanToEqualB(hora, tallo.horaFin)) array.add(tallo) }
        return array
    }

    fun getTallosAbiertosBeforeHour(tallosAsignados: ArrayList<TallosAsignados>, hora: String) : ArrayList<TallosAsignados> {
        val array = ArrayList<TallosAsignados>()
        tallosAsignados.forEach { tallo -> if (AwhereGreaterThanB(hora, tallo.horaInicio)) array.add(tallo) }
        return array
    }

    fun getMinutesTallos(tallosAsignados: ArrayList<TallosAsignados>, operarioId: Int) : Int{
        var minutos = 0
        tallosAsignados.forEach { tallo -> if (tallo.operarioId == operarioId) minutos += getMinuteDifference(tallo.horaFin, tallo.horaInicio) }
        return minutos
    }

    fun getRecesosValidos(recesos: ArrayList<Receso>, tallosAsignados: ArrayList<TallosAsignados>) : ArrayList<Receso>{
        val arrayList = HashSet<Receso>()

        for (operario in recesos){
            contextoInicio.setHoraVeintiCuatro(operario.horaInicio, contextoInicio)
            contextoFin.setHoraVeintiCuatro(operario.horaFin, contextoFin)
            for (j in tallosAsignados.indices) {
                if(AwhereGreaterThanB(tallosAsignados[j].horaInicio, contextoInicio.hora24) // 2
                    && AwhereGreaterThanToEqualB(contextoFin.hora24, tallosAsignados[j].horaInicio)){
                    arrayList.add(operario)
                }

                if(AwhereGreaterThanB(contextoFin.hora24, tallosAsignados[j].horaFin)
                    && AwhereGreaterThanB(tallosAsignados[j].horaFin, contextoInicio.hora24)){
                    arrayList.add(operario)
                }

                if(AwhereGreaterThanB(contextoInicio.hora24, tallosAsignados[j].horaInicio) // 4
                    && AwhereGreaterThanB(tallosAsignados[j].horaFin, contextoFin.hora24)){
                    arrayList.add(operario)
                }
            }

        }

        val array = ArrayList<Receso>()
        for (obj in arrayList)
            array.add(obj)
        return array
    }

    fun getMinutesRecesos(recesos: ArrayList<Receso>) : Int{
        var minutos = 0
        recesos.forEach { receso -> minutos += getMinuteDifference(receso.horaFin, receso.horaInicio) }
        return minutos
    }

    fun getCantTallosAsignados(tallosAsignados: ArrayList<TallosAsignados>, operarioId: Int) : Int{
        var cant = 0
        tallosAsignados.forEach { tallo -> if (tallo.operarioId == operarioId) cant += tallo.tallos}
        return cant
    }

    fun getEfectividad(tallosAsignados: ArrayList<TallosAsignados>, recesos: ArrayList<Receso>, operarioId: Int) =
        (getMinutesTallos(tallosAsignados, operarioId) - getMinutesRecesos(recesos)).toDouble()

    fun getEfectividad(efectividad: Double) : String {
        val enHoras = add0InValue(LocalTime.MIN.plus(Duration.ofMinutes(efectividad.toLong())).hour)
        val enMinutos = add0InValue(LocalTime.MIN.plus(Duration.ofMinutes(efectividad.toLong())).minute)
        return "$enHoras h $enMinutos min"
    }

    fun getTallosXHora(tallosAsignados: ArrayList<TallosAsignados>, recesos: ArrayList<Receso>, operarioId: Int): BigDecimal =
        BigDecimal((getCantTallosAsignados(tallosAsignados, operarioId).toDouble() /
                (getMinutesTallos(tallosAsignados, operarioId).toDouble() -
                        getMinutesRecesos(recesos).toDouble())) * 60.0).setScale(0, RoundingMode.HALF_EVEN)

    fun getTallosXHora(tallosCompletados: Int, minutosEfectivos: Int): BigDecimal =
        BigDecimal((tallosCompletados.toDouble() /
                minutosEfectivos.toDouble()) * 60.0).setScale(0, RoundingMode.HALF_EVEN)

    fun getRendimiento(tallosAsignados: ArrayList<TallosAsignados>, recesos: ArrayList<Receso>, rendimiento: Rendimiento, operarioId: Int): String {
        return if(rendimiento.rendimientoPorHora == 0) "N/A" else
        {
            "${BigDecimal(((getCantTallosAsignados(tallosAsignados , operarioId).toDouble() / ((getMinutesTallos(tallosAsignados, operarioId) -
                    getMinutesRecesos(recesos)).toDouble() *
                    ((rendimiento.rendimientoPorHora.toDouble() / 60.0)))) * 100.0)).setScale(0, RoundingMode.HALF_EVEN)}%"
        }
    }

    fun getRendimiento(tallosCompletados: Int, minutosEfectivos: Int, rendimiento: Rendimiento): Double {
        return if(rendimiento.rendimientoPorHora == 0) 0.0 else
        {
            BigDecimal(((tallosCompletados.toDouble() / ((minutosEfectivos).toDouble() *
                    ((rendimiento.rendimientoPorHora.toDouble() / 60.0)))) * 100.0)).setScale(0, RoundingMode.HALF_EVEN).toDouble()
        }
    }

    fun add0InValue(value: Int): String {
        return if(value<10) "0${value}" else "$value"
    }

    fun getArrayDays(): ArrayList<String> {
        val dias = ArrayList<String>()
        dias.add("L")
        dias.add("K")
        dias.add("M")
        dias.add("J")
        dias.add("V")
        dias.add("S")
        dias.add("D")
        return dias
    }

    fun getArrayTallos(): ArrayList<Int> {
        val tallos = ArrayList<Int>()
        tallos.add(0)
        tallos.add(0)
        tallos.add(0)
        tallos.add(0)
        tallos.add(0)
        tallos.add(0)
        tallos.add(0)
        return tallos
    }

    fun getArrayPorcentajes(): ArrayList<Double>{
        val porcentajes = ArrayList<Double>()
        porcentajes.add(0.0)
        porcentajes.add(0.0)
        porcentajes.add(0.0)
        porcentajes.add(0.0)
        porcentajes.add(0.0)
        porcentajes.add(0.0)
        porcentajes.add(0.0)
        return porcentajes
    }
}