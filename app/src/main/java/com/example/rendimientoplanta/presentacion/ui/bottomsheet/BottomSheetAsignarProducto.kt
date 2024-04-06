package com.example.rendimientoplanta.presentacion.ui.bottomsheet

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.model.AsignarProducto
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.utils.validators.CalcuTimeValidators
import com.example.rendimientoplanta.data.repository.AsignarProductoRepo
import com.example.rendimientoplanta.domain.impldomain.AsignarProductoCase
import com.example.rendimientoplanta.pattern.interprete.Contexto
import com.example.rendimientoplanta.presentacion.adapter.AdapterAsignarProducto
import com.example.rendimientoplanta.presentacion.adapter.SwipeAdapterDesasignarProducto
import com.example.rendimientoplanta.presentacion.factory.AsignarProductoFactory
import com.example.rendimientoplanta.presentacion.viewmodel.AsignarProductoVM
import com.example.rendimientoplanta.vo.Resource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_motivo.*
import kotlinx.android.synthetic.main.bottom_sheet_motivo.recyclerView
import kotlinx.android.synthetic.main.toolbar.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class BottomSheetAsignarProducto : BottomSheetDialogFragment() {
    private lateinit var adapterAsignarProducto: AdapterAsignarProducto
    private val args: BottomSheetAsignarProductoArgs by navArgs()
    private val TAG = "BottomSheetAsignar"
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            AsignarProductoFactory(AsignarProductoCase(AsignarProductoRepo()))
        ).get(AsignarProductoVM::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_asignar_producto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().toolbar.subtitle = "Asignar Producto"
        requireActivity().bottom_navigation.isVisible = true
        requireActivity().constaintLayout_toolbar.isVisible = true
    }

    override fun onStart() {
        super.onStart()
        imgExpand.setOnClickListener { dismiss() }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        setup(args.tallosAsignados!!.arrayList)

    }

    private fun setupAgain(arrayList: ArrayList<TallosAsignados>){
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        arrayList.reverse()
        setup(arrayList)
    }

    private fun showMessageException(mensaje : String, arrayList: ArrayList<TallosAsignados>){
        findNavController().navigate(BottomSheetAsignarProductoDirections.actionBottomSheetAsignarProductoToMessageBottomSheet(mensaje))
        setupAgain(arrayList)
    }

    private fun setup(arrayList: ArrayList<TallosAsignados>) {
        adapterAsignarProducto = AdapterAsignarProducto(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapterAsignarProducto
        recyclerView.layoutManager = LinearLayoutManager(context)
        val asignacionProductos = ArrayList<AsignarProducto>()
        for (tallo in arrayList) {
            asignacionProductos.add(
                AsignarProducto(
                    getHoraArmada(tallo.horaInicio),
                    getHoraArmada(tallo.horaFin), tallo.tallos, tallo.estado, tallo.tipo
                )
            )
        }
        asignacionProductos.reverse()
        //swipe
        val swipe = object : SwipeAdapterDesasignarProducto(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                arrayList.reverse()
                if(arrayList[viewHolder.adapterPosition].estado){
                    if (arrayList.size > 1) { //valida que haya mas de un elemento en la lista (si no pues nada mas lo dejamos cerrar)
                        //valida que el tallo seleccionado no tenga un tallo anterior abierto
                        if ((arrayList.size) != (viewHolder.adapterPosition+1) && arrayList[viewHolder.adapterPosition+1].estado) {
                            //mostrar mensaje de error porque no se puede desasignar ya que tiene una asignacion anterior abierta
                            Log.w(TAG, "No se puede desasignar el tallo ${arrayList[viewHolder.adapterPosition].uid} " +
                                    "con estado ${arrayList[viewHolder.adapterPosition].estado} " +
                                    "porque el tallo ${arrayList[viewHolder.adapterPosition+1].uid} " +
                                    "tiene estado ${arrayList[viewHolder.adapterPosition+1].estado}")
                            showMessageException("No se puede cerrar el tallo seleccionado " +
                                    "porque el tallo anterior aún se encuentra abierto. " +
                                    "Por favor cierre la asignación anterior para continuar.", arrayList)
                        } else {
                            //esto significa que el tallo seleccionado no tiene un tallo anterior abierto
                            Log.w(TAG, "El tallo seleccionado no tiene un tallo anterior abierto")
                            swichSwipeLeftRight(viewHolder, direction, arrayList)
                        }
                    } else {
                        //esto significa que el tallo seleccionado no tiene un tallo anterior abierto (este tallo es el primero)
                        Log.w(TAG,"El tallo seleccionado no tiene un tallo anterior abierto (este tallo es el primero)")
                        swichSwipeLeftRight(viewHolder, direction, arrayList)
                    }
                }else{
                    showMessageException("No se puede cerrar el tallo seleccionado " +
                            "porque el tallo ya ha sido cerrado.", arrayList)
                }
            }
        }
        ItemTouchHelper(swipe).attachToRecyclerView(recyclerView)
        //swipe
        adapterAsignarProducto.setListData(asignacionProductos)
        adapterAsignarProducto.notifyDataSetChanged()
    }

    fun swichSwipeLeftRight(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int,
        arrayList: ArrayList<TallosAsignados>
    ) {
            when (direction) {
                ItemTouchHelper.LEFT -> {
                    Log.w(
                        TAG,
                        "Tallo ${arrayList[viewHolder.adapterPosition].uid} obtenido en el Swipe LEFT [posicion=${viewHolder.adapterPosition}]"
                    )
                    desasignarProducto(viewHolder.adapterPosition, arrayList)
                }
                ItemTouchHelper.RIGHT -> {
                    Log.w(
                        TAG,
                        "Tallo ${arrayList[viewHolder.adapterPosition].uid} obtenido en el Swipe RIGHT [posicion=${viewHolder.adapterPosition}]"
                    )
                    desasignarProducto(viewHolder.adapterPosition, arrayList)
                }
            }

    }

    fun desasignarProductoConHora(
        adapterPosition: Int,
        arrayList: ArrayList<TallosAsignados>
    ) {
        var hourCurrent = 0
        var minsCurrent = 0
        var tallosSeleccionados = arrayList[adapterPosition]
        var tallosSiguientes = arrayList[adapterPosition]
        if (adapterPosition>0){
            tallosSiguientes = arrayList[adapterPosition-1]
        }

        TimePickerDialog(
            requireContext(), R.style.DatePickerDialogTheme_Green,
            { _, selectedHour, selectedMinute ->
                hourCurrent = selectedHour
                minsCurrent = selectedMinute

                val horaSeleccionada = construirHora(hourCurrent, minsCurrent)
                if(tallosSiguientes!=tallosSeleccionados && (CalcuTimeValidators.AwhereGreaterThanB(horaSeleccionada.hora24, tallosSiguientes.horaInicio))){
                    showMessageException("No se puede desasignar el tallo porque la hora seleccionada es mayor a la hora de inicio de la " +
                            "siguiente asignación.", arrayList)
                }else if(tallosSiguientes!=tallosSeleccionados && (CalcuTimeValidators.AwhereGreaterThanB(tallosSeleccionados.horaInicio,horaSeleccionada.hora24))){
                    showMessageException("No se puede desasignar el tallo porque la hora seleccionada es menor a la hora de inicio de la " +
                            "asignación.", arrayList)
                }
                else{
                   if((CalcuTimeValidators.AwhereGreaterThanB(tallosSeleccionados.horaInicio,horaSeleccionada.hora24))){
                       showMessageException("No se puede desasignar el tallo porque la hora seleccionada es menor a la hora de inicio de la " +
                               "asignación.", arrayList)
                    }else{
                       desasignarTallos(
                           tallosSeleccionados,
                           tallosSiguientes,
                           horaSeleccionada.hora24,
                           arrayList
                       )
                   }
                }
            },
            hourCurrent, minsCurrent, false
        ).show() //Yes 24 hour time
        setupAgain(arrayList)
    }

    fun desasignarProducto(adapterPosition: Int, arrayList: ArrayList<TallosAsignados>) {
        val hourCurrent = Integer.parseInt(SimpleDateFormat("HH:mm").format(Date()).split(":")[0])
        val minsCurrent = Integer.parseInt(SimpleDateFormat("HH:mm").format(Date()).split(":")[1])
        var tallosSeleccionados = arrayList[adapterPosition]
        var tallosSiguientes = arrayList[adapterPosition]
        if (adapterPosition>0){
            tallosSiguientes = arrayList[adapterPosition-1]
        }

        val horaActual = construirHora(hourCurrent, minsCurrent)
        if(CalcuTimeValidators.AwhereGreaterThanB(horaActual.hora24, tallosSeleccionados.horaInicio))
            desasignarTallos(tallosSeleccionados, tallosSiguientes, horaActual.hora24, arrayList)
        else
            showMessageException("No se pueden cerrar los tallos porque el operario no ha iniciado su asignación", arrayList)
    }

    fun construirHora(hora: Int, minutos: Int): Contexto {
        val contexto = Contexto()
        contexto.setHoraVeintiCuatro("${if(hora<10) "0${hora}" else hora}:${if(minutos<10) "0${minutos}" else minutos}", contexto)
        return contexto
    }

    fun desasignarTallos(
        tallosSeleccionados: TallosAsignados,
        tallosSiguientes: TallosAsignados,
        horaFin_horaInicio: String,
        arrayList: ArrayList<TallosAsignados>
    ){
        tallosSeleccionados.horaFin = horaFin_horaInicio
        tallosSeleccionados.estado = false
        tallosSeleccionados.sql = false

        val array = ArrayList<TallosAsignados>()
        array.add(tallosSeleccionados)
        if(tallosSeleccionados!=tallosSiguientes) {
            tallosSiguientes.horaInicio = horaFin_horaInicio
            tallosSiguientes.sql = false
            array.add(tallosSiguientes)
        }
        viewModel.setStem(array).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.w(TAG, "Actualizando tallo...")
                }
                is Resource.Success -> {
                    showMessageException("Tallos actualizados.", arrayList)
                }
                is Resource.Failure -> {
                    Log.w(TAG, "Error ${result.exception.message}")
                }
            }
        })
    }

    fun getHoraArmada(hora: String): String {
        val contexto = Contexto()
        return if (hora == "--:--")
            hora
        else {
            contexto.setHoraVeintiCuatro(hora, contexto)
            "${contexto.hora12} ${contexto.AMPM}"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "Hola que tal jejeje")
    }
}