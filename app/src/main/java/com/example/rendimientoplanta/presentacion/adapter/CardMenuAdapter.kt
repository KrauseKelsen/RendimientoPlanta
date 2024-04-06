package com.example.rendimientoplanta.presentacion.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.data.Set
import com.anychart.enums.HoverMode
import com.anychart.enums.TooltipDisplayMode
import com.anychart.enums.TooltipPositionMode
import com.example.rendimientoplanta.base.model.Chart
import com.example.rendimientoplanta.base.model.ChartPie
import com.example.rendimientoplanta.base.model.ChartVertical
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.utils.validators.CalcuValidators
import kotlinx.android.synthetic.main.card_menu_chart.view.*


class CardMenuAdapter(private val context: Context, val listaMenu: List<Chart>)  : RecyclerView.Adapter<BaseViewHolder<*>> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> = ChartViewHolder(
            LayoutInflater.from(
                    context
            ).inflate(R.layout.card_menu_chart, parent, false)
    )

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {

        when(holder){
            is ChartViewHolder -> holder.bind(listaMenu[position], position)
            else -> throw IllegalArgumentException("Se olvido de pasar el viewholder en el bind")
        }
    }

    override fun getItemCount(): Int = listaMenu.size
    inner class ChartViewHolder(itemView: View) : BaseViewHolder<Chart>(itemView) {

        override fun bind(item: Chart, position: Int) {
            if(item.nameClass == "ChartPie"){
                setupPieChart(item as ChartPie, itemView)

            }else if(item.nameClass == "ChartVertical"){
                setupVerticalChart(item as ChartVertical, itemView)
            }
        }
    }

    private fun setupPieChart(item: ChartPie, itemView: View) {

        var cont = 0
        val pie = AnyChart.pie()
        val dataEnties = ArrayList<DataEntry>()
        for (month in item.arrayString){
            val value = ValueDataEntry(month, item.arrayInt[cont])
            dataEnties.add(value)
            cont=+1
        }

        pie.palette(arrayOf("#0a883e", "#DB6302"))
        pie.data(dataEnties)
        itemView.chart.setChart(pie)
    }

    private fun setupVerticalChart(item: ChartVertical, itemView: View) {

        val vertical = AnyChart.vertical()

        vertical.animation(true)
            .title(item.titulo)

        val data: MutableList<DataEntry> = ArrayList()
        var cont = 0

        val result = CalcuValidators.calcularPorcentajes(item)
        for(dia in result.arrayString){
            if(result.arrayValue[cont] != 0){
                data.add(CustomDataEntry(dia, result.arrayValue[cont], result.arrayJumpLine[cont]))
            }
            cont +=1
        }

        val set = Set.instantiate()
        set.data(data)
        val barData = set.mapAs("{ x: 'x', value: 'value' }")
        val jumpLineData = set.mapAs("{ x: 'x', value: 'jumpLine' }")

        val bar = vertical.bar(barData)
        bar.labels().format("{%Value}")

        val jumpLine = vertical.jumpLine(jumpLineData)
        jumpLine.stroke("2 #0a883e")
        jumpLine.labels().enabled(false)

        vertical.yScale().minimum(0.0)

        vertical.labels(true)

        vertical.tooltip()
            .displayMode(TooltipDisplayMode.UNION)
            .positionMode(TooltipPositionMode.POINT)
            .unionFormat(
                    """function() {
      return 'Tallos procesados: ' + this.points[0].value +
        '\n' + 'Porcentaje semanal: ' + this.points[1].value + '%' + '';
    }"""
            )

        vertical.interactivity().hoverMode(HoverMode.BY_X)

        vertical.xAxis(true)
        vertical.yAxis(true)
        vertical.yAxis(0).labels().format("{%Value}")
        vertical.palette(arrayOf("#DB6302"))
        itemView.chart.setChart(vertical)
    }

    private class CustomDataEntry(x: String?, value: Number?, jumpLine: Number?) :
        ValueDataEntry(x, value) {
        init {
            setValue("jumpLine", jumpLine)
        }
    }
}