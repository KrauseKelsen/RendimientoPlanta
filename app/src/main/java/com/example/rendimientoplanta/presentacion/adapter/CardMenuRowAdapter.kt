package com.example.rendimientoplanta.presentacion.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rendimientoplanta.base.model.CardMenu
import com.example.rendimientoplanta.R
import kotlinx.android.synthetic.main.card_menu_row.view.*
import java.lang.IllegalArgumentException

class CardMenuRowAdapter(private val context: Context, val listaMenu: List<CardMenu>, private val itemClickListener: onCardMenuClickListener)  : RecyclerView.Adapter<BaseViewHolder<*>> (){

    interface onCardMenuClickListener{
        fun onItemClick(texto: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> = CardMenuViewHolder(LayoutInflater.from(context).inflate(
        R.layout.card_menu_row, parent,false))

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {

        when(holder){
            is CardMenuViewHolder -> holder.bind(listaMenu[position],position)
            else -> throw IllegalArgumentException("Se olvido de pasar el viewholder en el bind")
        }
    }

    override fun getItemCount(): Int = listaMenu.size
    inner class CardMenuViewHolder(itemView: View) : BaseViewHolder<CardMenu>(itemView) {

        override fun bind(item: CardMenu, position: Int) {
            itemView.setOnClickListener{itemClickListener.onItemClick(item.id)}
            itemView.cv_img.setImageResource(item.imagen)
        }
    }
}