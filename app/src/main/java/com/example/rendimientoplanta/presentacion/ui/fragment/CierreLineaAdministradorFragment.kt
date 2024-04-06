package com.example.rendimientoplanta.presentacion.ui.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.pojos.CierreLinea
import com.example.rendimientoplanta.base.pojos.CierreLineaLoad
import com.example.rendimientoplanta.base.pojos.Linea
import com.example.rendimientoplanta.data.repository.CierreLineaRepo
import com.example.rendimientoplanta.domain.impldomain.CierreLineaCase
import com.example.rendimientoplanta.presentacion.adapter.CardMenuCierreLineaAdapter
import com.example.rendimientoplanta.presentacion.factory.CierreLineaFactory
import com.example.rendimientoplanta.presentacion.viewmodel.CierreLineaVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_asignar_producto.progress_horizontal
import kotlinx.android.synthetic.main.fragment_cierre_linea_administrador.*


class CierreLineaAdministradorFragment : BaseFragment(true, true, "Cierre de línea"), CardMenuCierreLineaAdapter.OnClickListenerItem {
    private val TAG = "CierreLineaAdmistradorFragment"
    private val args: CierreLineaAdministradorFragmentArgs by navArgs()

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            CierreLineaFactory(CierreLineaCase(CierreLineaRepo()))
        ).get(CierreLineaVM::class.java)
    }

    private var lineasObtenidas = ArrayList<Linea>()
    private var nombresLineas = ArrayList<String>()
    private var lineaSeleccionada = ""
    override fun getViewID():Int = R.layout.fragment_cierre_linea_administrador

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        spinnerLinea.adapter = null
        lineasObtenidas = ArrayList()
        nombresLineas = ArrayList()
        lineaSeleccionada = ""
        obtenerLineas()
    }

    fun obtenerLineas(){
        viewModel.getLineas().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Obteniendo lineas...")
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "Lineas obtenidas ${result.data}")
                    lineasObtenidas = result.data
                    if(lineasObtenidas.size!=0) initSpinnerLinea()
                }
                is Resource.Failure -> {
                    findNavController().navigate(CierreLineaAdministradorFragmentDirections
                        .actionCierreLineaAdmistradorFragmentToMessageBottomSheet
                            ("Error al obtener las líneas: \nMotivo_: ${result.exception.message}"))
                    Log.w(TAG, "${MessageBuilder.sFailure}, no se pudieron obtener las lineas")
                }
            }
        })
    }

    private fun initSpinnerLinea() {
        lineasObtenidas.forEach{obj -> nombresLineas.add(obj.nombre)}

        nombresLineas.sortWith { o1, o2 -> Integer.parseInt(o1.substring(6)).compareTo(Integer.parseInt(o2.substring(6)))}

        spinnerLinea.adapter = ArrayAdapter(requireContext(),
            R.layout.custom_spinner_list_item, nombresLineas)
        spinnerLineaOnItemSelectedListener()
    }

    private fun spinnerLineaOnItemSelectedListener() {
        spinnerLinea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                lineaSeleccionada = ""
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                lineaSeleccionada = nombresLineas[position]
                setupRecyclerView(getCierresLinea())
            }
        }
    }

    private fun getCierresLinea(): ArrayList<CierreLineaLoad> {
        val arrayList = ArrayList<CierreLineaLoad>()
        args.cierresLinea!!.arrayList.forEach { cierreLinea -> if(lineaSeleccionada.substring(6).toInt()==cierreLinea.lineaId) arrayList.add(cierreLinea) }
        return arrayList
    }

    private fun setupRecyclerView(cierresLinea: ArrayList<CierreLineaLoad>) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = CardMenuCierreLineaAdapter(requireContext(), cierresLinea, this)
        if(cierresLinea.size==0) findNavController().navigate(
            CierreLineaAdministradorFragmentDirections
                .actionCierreLineaAdmistradorFragmentToMessageBottomSheet
                    ("La $lineaSeleccionada no posee cierres registrados, por favor configuere su cuenta con la $lineaSeleccionada, " +
                        "luego comience un cierre de operarios y finalicelo para continuar."))
    }

    override fun onItemClick(cierreLinea: CierreLineaLoad) {
        findNavController().navigate(CierreLineaAdministradorFragmentDirections.actionCierreLineaAdmistradorFragmentToCierreOperarioLoadFragment(cierreLinea))
    }
}