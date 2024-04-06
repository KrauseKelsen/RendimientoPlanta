package com.example.rendimientoplanta.presentacion.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.pojos.CierreOperario
import com.example.rendimientoplanta.base.pojos.CierreOperarioLoad
import com.example.rendimientoplanta.base.pojos.Motivo
import com.example.rendimientoplanta.pattern.interprete.Contexto
import com.example.rendimientoplanta.pattern.interprete.Reloj
import kotlinx.android.synthetic.main.card_menu_cierre_linea.view.*
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime


class CardMenuCierreOperarioAdapter(private val context: Context, val listaMenu: List<CierreOperarioLoad>)  : RecyclerView.Adapter<BaseViewHolder<*>> () {
    var contexto = Contexto()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> = CierreOperarioViewHolder(
            LayoutInflater.from(context).inflate(R.layout.card_menu_cierre_linea, parent, false)
    )

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {

        when(holder){
            is CierreOperarioViewHolder -> holder.bind(listaMenu[position], position)
            else -> throw IllegalArgumentException("Se olvido de pasar el viewholder en el bind")
        }
    }

    override fun getItemCount(): Int = listaMenu.size
    inner class CierreOperarioViewHolder(itemView: View) : BaseViewHolder<CierreOperarioLoad>(itemView) {


        //HAY UN DESPICHE TOTAL EN EL CIERRE, LOS DATOS SI SE GUARDAN BIEN PERO NO SE MUESTRAN BIEN
        // EL CIERREOPERARIO LOAD NO TRAE LOS TALLOS PARCIALES

        override fun bind(item: CierreOperarioLoad, position: Int) {
            itemView.tanda.text = "Operario: ${item.operario}"
            itemView.tallosAsignados.text = "Tallos asignados: ${item.tallosAsignados}"
            itemView.tallosAlCierre.text = "Tallos al cierre: ${item.tallosParciales+item.tallosCompletados}"
            itemView.tallosPorHora.text = "${item.tallosXhora} tallos por hora"
            itemView.minutosEfectivos.text = "Efectividad: ${LocalTime.MIN.plus(Duration.ofMinutes(item.minutosEfectivos.toLong())).hour} h ${LocalTime.MIN.plus(Duration.ofMinutes(item.minutosEfectivos.toLong())).minute} min"
            itemView.rendimiento.text = "${item.rendimientoXhora.toInt()}%"


        }
    }

}