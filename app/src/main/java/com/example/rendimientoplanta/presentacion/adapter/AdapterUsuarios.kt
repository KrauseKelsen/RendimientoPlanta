package com.example.rendimientoplanta.presentacion.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.pojos.User
import kotlinx.android.synthetic.main.row_usuarios.view.*
import java.util.*
import java.util.stream.Collectors

class AdapterUsuarios(
    private val context: Context, private val itemClickListenerDisable: OnUsuariosClickListenerDisable):
    RecyclerView.Adapter<AdapterUsuarios.ViewHolderUsuarios>(){
    private val TAG = "AdapterUsuarios"
    interface OnUsuariosClickListenerDisable{
        fun onItemClickDisable(user: User)
    }

    private var dataList = mutableListOf<User>()
    private var dataListOriginal = mutableListOf<User>()

    fun setListData(data: MutableList<User>){
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
                    it.nombre.contains(strSearch, true) || it.apellidos.contains(strSearch, true)
                            || it.rol.contains(strSearch)|| it.abrev.contains(strSearch)  }.collect(
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
    inner class ViewHolderUsuarios(itemView: View): RecyclerView.ViewHolder(itemView){
        //esta funcion blindea los datos del elemento a la card, esta información viene del onBindViewHolder
        fun blindearVista(user: User){
            itemView.tv_nombreUsuario.text = if ("${user.nombre} ${user.apellidos}".length > 20) "${"${user.nombre} ${user.apellidos}".substring(0, 17)}..." else "${user.nombre} ${user.apellidos}"
            itemView.tv_rolUsuario.text = "${if ("${user.rol}"=="Administrador de planta") "Adm. planta" else "${user.rol}"}"
            itemView.tv_fincaLinea.text = "${if ("${user.abrev}".length > 10) "${"${user.abrev}".substring(0, 7)}..." else "${user.abrev}"}, L-${user.linea}"

            if(user.estado) {
                itemView.tv_enable.text = "Habilitado"
                itemView.imgEnable.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_eye_green))
            }else {
                itemView.tv_enable.text = "Deshabil..."
                itemView.imgEnable.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_eye_no))
            }

            itemView.ly_Enable.setOnClickListener { itemClickListenerDisable.onItemClickDisable(user) }
        }
    }

    /**
     * /ViewType es para meter diferentes cartas en el recycler view pero en este caso no se usa
     * parent es el contenedor que encerrar los datos
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderUsuarios = ViewHolderUsuarios(
        LayoutInflater.from(context).inflate(R.layout.row_usuarios, parent, false))

    override fun onBindViewHolder(holder: ViewHolderUsuarios, position: Int) = holder.blindearVista(dataList[position])

    override fun getItemCount(): Int = if(dataList.size > 0){ dataList.size }else{ 0 }
}