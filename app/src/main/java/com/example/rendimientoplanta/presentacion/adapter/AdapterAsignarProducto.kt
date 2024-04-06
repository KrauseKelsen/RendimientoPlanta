package com.example.rendimientoplanta.presentacion.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rendimientoplanta.base.model.AsignarProducto
import com.example.rendimientoplanta.R
import kotlinx.android.synthetic.main.row_productos_asignados.view.*

class AdapterAsignarProducto(private val context: Context): RecyclerView.Adapter<AdapterAsignarProducto.ViewHolderAsignarProducto>(){

    //esto es una lista vacia donde va el arraylist que pasa el adaptador
    private var dataList = mutableListOf<AsignarProducto>()

    fun setListData(data: MutableList<AsignarProducto>){
        dataList = data
    }
    /**
     * El view holder recibe una vista (en este caso las cardviews) para poderlos inicializar
     */
    inner class ViewHolderAsignarProducto(itemView: View): RecyclerView.ViewHolder(itemView){
        //esta funcion blindea los datos del elemento a la card, esta información viene del onBindViewHolder
        fun blindearVista(asignarProducto: AsignarProducto){
            itemView.tvHoraFin.text = asignarProducto.horaFin
            itemView.tvHoraInicio.text = asignarProducto.horaInicio
            itemView.tv_cantidad.text = "${asignarProducto.cantidad} tallos"
            if(asignarProducto.asignado){
                itemView.img_assignment.setImageResource(R.drawable.ic_assignment_late)
                itemView.tv_assignment.text = asignarProducto.tipo
            }
            else{
                itemView.img_assignment.setImageResource(R.drawable.ic_assignment)
                itemView.tv_assignment.text = asignarProducto.tipo
            }
        }
    }

    /**
     * /ViewType es para meter diferentes cartas en el recycler view pero en este caso no se usa
     * parent es el contenedor que encerrar los datos
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAsignarProducto = ViewHolderAsignarProducto(
        LayoutInflater.from(context).inflate(R.layout.row_productos_asignados, parent, false))

    override fun onBindViewHolder(holder: ViewHolderAsignarProducto, position: Int) = holder.blindearVista(dataList[position])

    override fun getItemCount(): Int = if(dataList.size > 0){ dataList.size }else{ 0 }
}