package com.example.rendimientoplanta.presentacion.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rendimientoplanta.base.model.CardMenu
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.presentacion.adapter.CardMenuRowAdapter
import kotlinx.android.synthetic.main.fragment_procesos.*

class OpcionesFragment : BaseFragment(true, true, "Opciones de administrador"),
    CardMenuRowAdapter.onCardMenuClickListener {
    private val TAG = "OpcionesFragment"

    override fun getViewID(): Int = R.layout.fragment_opciones

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val list: List<CardMenu> = if(user.rol == "Administrador"){
            listOf(
                CardMenu(R.drawable.card_menu_usuarios, 1),
                CardMenu(R.drawable.card_menu_operarios, 2),
                CardMenu(R.drawable.card_menu_fincas, 3)
//                ,CardMenu(R.drawable.card_menu_roles, 4)
            )
        }else{
            listOf(
                CardMenu(R.drawable.card_menu_operarios, 2)
            )
        }

        recyclerView.adapter = CardMenuRowAdapter(requireContext(), list, this)
    }

    override fun onItemClick(texto: Int) {
        when (texto) {
            1 -> {
                Log.w(TAG, "Usuarios")
                findNavController().navigate(R.id.action_opcionesFragment_to_registroUsuarioFragment)
            }
            2 -> {
                Log.w(TAG, "Operarios")
                findNavController().navigate(R.id.action_opcionesFragment_to_registroOperarioFragment)
            }
            3 -> {
                Log.w(TAG, "Fincas")
                findNavController().navigate(R.id.action_opcionesFragment_to_fincaFragment)
            }
//            4 -> {
//                Log.w(TAG, "Roles & Permisos")
//                findNavController().navigate(OpcionesFragmentDirections.actionOpcionesFragmentToMessageBottomSheet("Estamos en mantenimiento"))
//            }
        }
    }
}