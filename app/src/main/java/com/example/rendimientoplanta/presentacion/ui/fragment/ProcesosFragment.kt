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

class ProcesosFragment : BaseFragment(true, true, "Procesos"),
    CardMenuRowAdapter.onCardMenuClickListener {
    private val TAG = "ProcesosFragment"

    override fun getViewID(): Int = R.layout.fragment_procesos

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val list = listOf(
            CardMenu(R.drawable.card_menu_operarios_en_linea, 1),
            CardMenu(R.drawable.card_menu_jornada, 2),
            CardMenu(R.drawable.card_menu_receso, 3)//,
            //CardMenu(R.drawable.card_menu_tiempo, 4)
        )
        recyclerView.adapter = CardMenuRowAdapter(requireContext(), list, this)
    }

    override fun onItemClick(id: Int) {
        when (id) {
            1 -> {
                Log.w(TAG, "Operarios")
                findNavController().navigate(R.id.action_procesosFragment_to_operarioFragment)
            }
            2 -> {
                Log.w(TAG, "Jornada")
                findNavController().navigate(R.id.action_procesosFragment_to_jornadaFragment)
            }
            3 -> {
                Log.w(TAG, "Recesos")
                findNavController().navigate(R.id.action_procesosFragment_to_recesosFragment)
            }
//            4 -> {
//                Log.w(TAG, "Tiempo muerto")
//                findNavController().navigate(R.id.action_procesosFragment_to_tiempoMuertoFragment)
//            }
        }
    }
}