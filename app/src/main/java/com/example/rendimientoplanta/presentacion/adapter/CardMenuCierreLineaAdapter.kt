package com.example.rendimientoplanta.presentacion.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.pojos.CierreLinea
import com.example.rendimientoplanta.base.pojos.CierreLineaLoad
import com.example.rendimientoplanta.pattern.interprete.Contexto
import com.example.rendimientoplanta.pattern.interprete.Reloj
import kotlinx.android.synthetic.main.card_menu_cierre_linea.view.*
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime


class CardMenuCierreLineaAdapter(private val context: Context, val listaMenu: ArrayList<CierreLineaLoad>, private val itemClickListener: OnClickListenerItem)  : RecyclerView.Adapter<BaseViewHolder<*>> () {
    var contexto = Contexto()

    interface OnClickListenerItem{
        fun onItemClick(cierreLinea: CierreLineaLoad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> = CierreLineaViewHolder(
            LayoutInflater.from(context).inflate(R.layout.card_menu_cierre_linea, parent, false)
    )

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {

        when(holder){
            is CierreLineaViewHolder -> holder.bind(listaMenu[position], position)
            else -> throw IllegalArgumentException("Se olvido de pasar el viewholder en el bind")
        }
    }

    override fun getItemCount(): Int = listaMenu.size
    inner class CierreLineaViewHolder(itemView: View) : BaseViewHolder<CierreLineaLoad>(itemView) {

        override fun bind(item: CierreLineaLoad, position: Int) {
            itemView.card_menu_cierre_linea_cv.setOnClickListener { itemClickListener.onItemClick(item) }
            itemView.tanda.text = "Cierre de las ${updateContexto(item.horaInicio).hora12} ${updateContexto(item.horaInicio).AMPM}"
            itemView.tallosAsignados.text = "Tallos asignados: ${item.tallosAsignados}"
            itemView.tallosAlCierre.text = "Tallos al cierre: ${item.tallosCompletados+item.tallosParciales}"
            itemView.tallosPorHora.text = "${item.tallosXhora} tallos por hora"
            itemView.minutosEfectivos.text = "Efectividad: ${LocalTime.MIN.plus(Duration.ofMinutes(item.minutosEfectivos.toLong())).hour} h ${LocalTime.MIN.plus(Duration.ofMinutes(item.minutosEfectivos.toLong())).minute} min"
            itemView.rendimiento.text = "${item.rendimientoXhora.toInt()}%"


        }
    }

    fun updateContexto(hora: String): Contexto {
        contexto.hora24 = hora
        Reloj().interpretar24(contexto)
        return contexto
    }

}