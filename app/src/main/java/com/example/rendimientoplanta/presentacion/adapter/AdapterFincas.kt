package com.example.rendimientoplanta.presentacion.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.pojos.Finca
import kotlinx.android.synthetic.main.row_fincas.view.*
import java.util.*
import java.util.stream.Collectors

class AdapterFincas(
    private val context: Context, private val itemClickListener: OnFincasClickListener):
    RecyclerView.Adapter<AdapterFincas.ViewHolderFincas>(){
    private val TAG = "AdapterFincas"

    interface OnFincasClickListener{
        fun itemClickListener(finca: Finca)
    }

    private var dataList = mutableListOf<Finca>()
    private var dataListOriginal = mutableListOf<Finca>()

    fun setListData(data: MutableList<Finca>){
        dataList = data
        dataListOriginal = mutableListOf() // va mantener el listado original y el que va cambiar es el dataList
        dataListOriginal.addAll(data) // pero este original siempre va mantener a todos
    }

    fun filter(strSearch: String){
        if(strSearch.isEmpty()){ //si no ingresan nada pues se muestra toda la lista
            dataList.clear()
            dataList.addAll(dataListOriginal)
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dataList.clear()
                val collect = dataListOriginal.stream().filter {
                    it.abreviatura.contains(strSearch, true) || it.nombre.contains(strSearch, true)
                            || it.uid.toString().contains(strSearch)  }.collect(
                    Collectors.toList())
                dataList.addAll(collect)
            }else{ // para versiones viejas pues no servirá el filtro porque que pereza jaja, igual el link para hacer esta aqui: https://www.youtube.com/watch?v=TSnWWhUx1V4
                dataList.clear()
                dataList.addAll(dataListOriginal)
            }
        }
        notifyDataSetChanged()
    }

    /**
     * El view holder recibe una vista (en este caso las cardviews) para poderlos inicializar
     */
    inner class ViewHolderFincas(itemView: View): RecyclerView.ViewHolder(itemView){
        //esta funcion blindea los datos del elemento a la card, esta información viene del onBindViewHolder
        fun blindearVista(finca: Finca){
            itemView.tv_idFinca.text = "ID: ${finca.uid}"
            itemView.nombreFinca.text = "${finca.nombre}"
            itemView.abreviatura.text = "${finca.abreviatura}"

            if(finca.estado) {
                itemView.tv_enable.text = "Habilitado"
                itemView.imgEnable.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_eye_green))
            }else {
                itemView.tv_enable.text = "Deshabil..."
                itemView.imgEnable.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_eye_no))
            }

            itemView.ly_Enable.setOnClickListener { itemClickListener.itemClickListener(finca) }
        }

    }

    /**
     * /ViewType es para meter diferentes cartas en el recycler view pero en este caso no se usa
     * parent es el contenedor que encerrar los datos
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFincas = ViewHolderFincas(
        LayoutInflater.from(context).inflate(R.layout.row_fincas, parent, false))

    override fun onBindViewHolder(holder: ViewHolderFincas, position: Int) = holder.blindearVista(dataList[position])

    override fun getItemCount(): Int = if(dataList.size > 0){ dataList.size }else{ 0 }
}