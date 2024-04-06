package com.example.rendimientoplanta.base.utils.validators

import com.example.rendimientoplanta.base.model.ChartVertical
import java.math.BigDecimal
import java.math.RoundingMode

object CalcuValidators {
    val TAG = "CalcuValidators"
    fun calcularPorcentajes(item: ChartVertical): ChartVertical {
        var cantDias = 0
        var tallosCompletados = 0
        for(index in item.arrayString.indices) {
            if(item.arrayValue[index] != 0){
                tallosCompletados += item.arrayValue[index]
                cantDias += 1
            }
        }

        for(index in item.arrayValue.indices){
            if(item.arrayValue[index] != 0){
                val value1 = (item.arrayValue[index]/cantDias).toDouble()
                val value2 = value1*100.0
                val value3 = value2/((tallosCompletados/cantDias).toDouble())
                item.arrayJumpLine[index] = BigDecimal(value3).setScale(2, RoundingMode.HALF_EVEN).toDouble()
            }
        }

        if(cantDias !=0){
            item.arrayString.add("AVG")
            item.arrayValue.add(tallosCompletados/cantDias)
            item.arrayJumpLine.add(100.0)
        }

        return item
    }

}