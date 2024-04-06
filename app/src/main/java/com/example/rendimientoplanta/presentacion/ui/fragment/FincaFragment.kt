package com.example.rendimientoplanta.presentacion.ui.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.pojos.Finca
import com.example.rendimientoplanta.data.repository.FincaRepo
import com.example.rendimientoplanta.domain.impldomain.FincaCase
import com.example.rendimientoplanta.presentacion.adapter.AdapterFincas
import com.example.rendimientoplanta.presentacion.factory.FincaFactory
import com.example.rendimientoplanta.presentacion.viewmodel.FincaVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_fincas.*

@RequiresApi(Build.VERSION_CODES.M)
class FincaFragment : BaseFragment(true, true, "Finca") , AdapterFincas.OnFincasClickListener, SearchView.OnQueryTextListener{
    private val TAG = "FincaFragment"
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            FincaFactory(FincaCase(FincaRepo()))
        ).get(FincaVM::class.java)
    }

    private lateinit var adapterFincas: AdapterFincas
    private lateinit var misFincas: ArrayList<Finca>

    override fun getViewID():Int = R.layout.fragment_fincas


    fun initListener(){
        svSearchView.setOnQueryTextListener(this)
    }

    //se ejecuta cuando se presiona el texto
    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    //queda escuchando cada letra
    override fun onQueryTextChange(newText: String?): Boolean {
        adapterFincas.filter(newText!!)
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setAdapter()
        getMisFincas()
    }

    fun getMisFincas() {
        viewModel.getFincas().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Obteniendo fincas...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Fincas obtenidas ${result.data}")
                    misFincas = result.data
                    setRecyclerView()
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure -> {
                    findNavController().navigate(FincaFragmentDirections
                        .actionFincaFragmentToMessageBottomSheet("Error al obtener las fincas. \nMotivo: ${result.exception.message.toString()}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }


    fun setAdapter(){
        adapterFincas = AdapterFincas(requireContext(), this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapterFincas
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setRecyclerView() {
        misFincas.sortWith { o1, o2 -> (o1.uid).compareTo(o2.uid)}

        adapterFincas.setListData(misFincas)
        adapterFincas.notifyDataSetChanged()
        initListener()
    }

    override fun itemClickListener(finca: Finca) {
        finca.estado = !finca.estado
        viewModel.putFincas(finca.uid, finca.nombre, finca.abreviatura, finca.estado, user).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Actualizando finca...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "${result.data}")
                    setRecyclerView()
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure -> {
                    findNavController().navigate(FincaFragmentDirections
                        .actionFincaFragmentToMessageBottomSheet("Error al actualizar las fincas. \nMotivo: ${result.exception.message.toString()}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }
}