package com.example.rendimientoplanta.presentacion.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rendimientoplanta.R
import kotlinx.android.synthetic.main.row_motivo.view.*
import com.example.rendimientoplanta.base.pojos.Motivo
import com.example.rendimientoplanta.pattern.interprete.Contexto

class AdapterMotivo(private val context: Context, private val itemClickListenerTrash: OnMotivoClickListenerTrash): RecyclerView.Adapter<AdapterMotivo.ViewHolderMotivo>(){

    interface OnMotivoClickListenerTrash{
        fun onItemClickTrash(motivo: Motivo)
    }

    //esto es una lista vacia donde va el arraylist que pasa el adaptador
    private var dataList = mutableListOf<Motivo>()

    fun setListData(data: MutableList<Motivo>){
        dataList = data
    }
    /**
     * El view holder recibe una vista (en este caso las cardviews) para poderlos inicializar
     */
    inner class ViewHolderMotivo(itemView: View): RecyclerView.ViewHolder(itemView){
        //esta funcion blindea los datos del elemento a la card, esta información viene del onBindViewHolder
        fun blindearVista(motivo: Motivo){
            val contextoInicio = Contexto()
            contextoInicio.setHoraVeintiCuatro(motivo.horaInicio, contextoInicio)
            val contextoFin = Contexto()
            contextoFin.setHoraVeintiCuatro(motivo.horaFin, contextoFin)
            itemView.ly_eliminar.setOnClickListener { itemClickListenerTrash.onItemClickTrash(motivo) }
            itemView.tvHoraInicio.text = "${contextoInicio.hora12} ${contextoInicio.AMPM}"
            itemView.tvHoraFin.text = "${contextoFin.hora12} ${contextoFin.AMPM}"

            if(motivo.motivo=="Desayuno"){
                itemView.tv_motivo.text = "${motivo.motivo}  "
                itemView.imgReceso.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_breakfast))
            }
            if(motivo.motivo=="Almuerzo"){
                itemView.tv_motivo.text = "${motivo.motivo}  "
                itemView.imgReceso.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_eat))
            }
            if(motivo.motivo=="Café tarde"){
                itemView.tv_motivo.text = "${motivo.motivo}  "
                itemView.imgReceso.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_breakfast))
            }
            if(motivo.motivo=="Cena"){
                itemView.tv_motivo.text = "${motivo.motivo}           "
                itemView.imgReceso.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_dinner))
            }
            if(motivo.motivo=="Pausa activa"){
                itemView.tv_motivo.text = "${motivo.motivo.subSequence(0, 9)}..."
                itemView.imgReceso.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_pause))
            }
            if(motivo.motivo=="Otra"){
                itemView.tv_motivo.text = "${motivo.motivo}            "
                itemView.imgReceso.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_other))
            }
        }
    }

    /**
     * /ViewType es para meter diferentes cartas en el recycler view pero en este caso no se usa
     * parent es el contenedor que encerrar los datos
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMotivo = ViewHolderMotivo(
        LayoutInflater.from(context).inflate(R.layout.row_motivo, parent, false))

    override fun onBindViewHolder(holder: ViewHolderMotivo, position: Int) = holder.blindearVista(dataList[position])

    override fun getItemCount(): Int = if(dataList.size > 0){ dataList.size }else{ 0 }
}