package com.example.rendimientoplanta.presentacion.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.OperarioLinea_TallosAsignados
import kotlinx.android.synthetic.main.row_operarios_en_linea.view.*

class AdapterOperariosEnLinea(
    private val context: Context, private val itemClickListenerTrash: OnOperariosClickListenerTrash): RecyclerView.Adapter<AdapterOperariosEnLinea.ViewHolderOperarios>(){
    private val TAG = "AdapterOperarios"

    interface OnOperariosClickListenerTrash{
        fun onItemClickTrash(obj: OperarioLinea)
    }

    private var dataList = mutableListOf<OperarioLinea_TallosAsignados>()

    fun setListData(data: MutableList<OperarioLinea_TallosAsignados>){
        dataList = data
    }
    /**
     * El view holder recibe una vista (en este caso las cardviews) para poderlos inicializar
     */
    inner class ViewHolderOperarios(itemView: View): RecyclerView.ViewHolder(itemView){
        //esta funcion blindea los datos del elemento a la card, esta información viene del onBindViewHolder
        fun blindearVista(operario: OperarioLinea_TallosAsignados){
            itemView.nombreOperario.text = "${operario.operarioLinea.nombre} ${operario.operarioLinea.apellidos}"
            if(operario.tallosAsignados) {
                itemView.nombreOperario.setTextColor(ContextCompat.getColor(context, R.color.primary_soft))
                itemView.imgTrash.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_trash))
            }
            else {
                itemView.nombreOperario.setTextColor(ContextCompat.getColor(context, R.color.verde_soft))
                itemView.imgTrash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_trash_green))
            }
            itemView.imgTrash.setOnClickListener { itemClickListenerTrash.onItemClickTrash(operario.operarioLinea) }
        }
    }

    /**
     * /ViewType es para meter diferentes cartas en el recycler view pero en este caso no se usa
     * parent es el contenedor que encerrar los datos
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderOperarios = ViewHolderOperarios(
        LayoutInflater.from(context).inflate(R.layout.row_operarios_en_linea, parent, false))

    override fun onBindViewHolder(holder: ViewHolderOperarios, position: Int) = holder.blindearVista(dataList[position])

    override fun getItemCount(): Int = if(dataList.size > 0){ dataList.size }else{ 0 }
}