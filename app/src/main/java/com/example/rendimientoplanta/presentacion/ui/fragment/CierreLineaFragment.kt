package com.example.rendimientoplanta.presentacion.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.pojos.CierreLineaLoad
import com.example.rendimientoplanta.presentacion.adapter.CardMenuCierreLineaAdapter
import kotlinx.android.synthetic.main.fragment_asignar_producto.*
import kotlinx.android.synthetic.main.fragment_cierre_linea.recyclerView


class CierreLineaFragment : BaseFragment(true, true, "Cierre de línea"), CardMenuCierreLineaAdapter.OnClickListenerItem {
    private val TAG = "CierreLineaFragment"
    private val args: CierreLineaFragmentArgs by navArgs()

    override fun getViewID():Int = R.layout.fragment_cierre_linea

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showProgressBar(false, progress_horizontal)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        setupRecyclerView()
    }


    private fun setupRecyclerView() {
        recyclerView.adapter = CardMenuCierreLineaAdapter(requireContext(), args.cierresLinea!!.arrayList, this)
    }

    override fun onItemClick(cierreLinea: CierreLineaLoad) {
        findNavController().navigate(CierreLineaFragmentDirections.actionCierreLineaFragmentToCierreOperarioLoadFragment(cierreLinea))
    }
}