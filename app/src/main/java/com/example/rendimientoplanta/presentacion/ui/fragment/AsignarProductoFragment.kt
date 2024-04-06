package com.example.rendimientoplanta.presentacion.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.pojos.ArrayListTallosAsignados
import com.example.rendimientoplanta.base.pojos.Jornada
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.utils.validators.FieldDecimalValidators
import com.example.rendimientoplanta.data.repository.AsignarProductoRepo
import com.example.rendimientoplanta.domain.impldomain.AsignarProductoCase
import com.example.rendimientoplanta.pattern.interprete.Contexto
import com.example.rendimientoplanta.presentacion.factory.AsignarProductoFactory
import com.example.rendimientoplanta.presentacion.viewmodel.AsignarProductoVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_asignar_producto.*

class AsignarProductoFragment : BaseFragment(true, true, "Asignar Producto"){
    private val TAG = "AsignarProductoFragment"
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            AsignarProductoFactory(AsignarProductoCase(AsignarProductoRepo()))
        ).get(AsignarProductoVM::class.java)
    }

    private var operariosEnLinea = ArrayList<OperarioLinea>()
    private var tallosAsignados = ArrayList<TallosAsignados>()
    private var nombresOperariosEnLinea = ArrayList<String>()
    private var operarioEnLineaSeleccionado = ""
    private var nombresTipoTallo = ArrayList<String>()
    private var tipoTalloSeleccionado = ""
    private var operarioIDSeleccionado = 0
    override fun getViewID():Int = R.layout.fragment_asignar_producto

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnVer.setOnClickListener { if(operarioEnLineaSeleccionado!="") getStems()
        else findNavController().navigate(
            AsignarProductoFragmentDirections.actionAsignarProductoFragmentToMessageBottomSheet("Debe seleccionar un operario para visualizar el historial de tallos asignados"))}
        getOperariosEnLinea()
        initSpinnerTipoTallo()
        FieldDecimalValidators.setupListeners(textInputLayout_cantidad,edit_text_cantidad,resources)
        btnGuardar.setOnClickListener { if (FieldDecimalValidators.isValidate(textInputLayout_cantidad, edit_text_cantidad, resources)) asignarTallos() }
    }

    private fun getStems() {
        if(tallosAsignados.size!=0)
            findNavController().navigate(AsignarProductoFragmentDirections.actionAsignarProductoFragmentToBottomSheetAsignarProducto(
                ArrayListTallosAsignados(tallosAsignados)
            ))
        else
            findNavController().navigate(AsignarProductoFragmentDirections.actionAsignarProductoFragmentToMessageBottomSheet("El operario en línea no posee tallos asignados"))
    }

    private fun asignarTallos() {
        if(operarioEnLineaSeleccionado!=""){
            operariosEnLinea.forEach{obj ->


                if(obj.operarioId == operarioIDSeleccionado) {

                    if(tipoTalloSeleccionado!=""){
                        getJornada(obj)
                    }else{
                        findNavController().navigate(
                            AsignarProductoFragmentDirections.actionAsignarProductoFragmentToMessageBottomSheet("Debe seleccionar un tipo de tallo para asignar" ))
                    }

                }
            }

        }else{
            findNavController().navigate(
                AsignarProductoFragmentDirections.actionAsignarProductoFragmentToMessageBottomSheet("Debe seleccionar un operario para asignar los tallos" ))
        }


    }

    private fun getJornada(operarioLinea: OperarioLinea){
        viewModel.getJornada(user).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sLoading} Obteniendo jornada")
                }
                is Resource.Success -> {
                    if(result.data.size!=0)
                        asignarTallos(operarioLinea, result.data[0])
                    else
                        findNavController().navigate(AsignarProductoFragmentDirections.actionAsignarProductoFragmentToMessageBottomSheet(
                            "No existe una jornada iniciada el día de hoy en la línea ${user.linea} de la finca ${user.finca}.\n" +
                                    "Por favor registra una jornada antes de comenzar la asignación de tallos a los operarios"))
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure -> {
                    findNavController().navigate(AsignarProductoFragmentDirections.actionAsignarProductoFragmentToMessageBottomSheet(
                        "Error al validar la existencia de una jornada." +
                                "\nMotivo: ${result.exception.message.toString()}"
                    ))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    fun asignarTallos(operarioLinea: OperarioLinea, jornada: Jornada){
        viewModel.setStems(operarioLinea, user, edit_text_cantidad.text.toString().toInt(), jornada, tipoTalloSeleccionado).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sLoading} Asignando tallos")
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sSuccess} Tallos asignados: ${result.data}")
                    findNavController().navigate(
                        AsignarProductoFragmentDirections.actionAsignarProductoFragmentToMessageBottomSheet
                            ("Han sido asignados ${edit_text_cantidad.text.toString().toInt()} tallos al operario $operarioEnLineaSeleccionado"))
                    setTallosAsignados()
                    limpiarCampos()
                }
                is Resource.Failure -> {
                    findNavController().navigate(AsignarProductoFragmentDirections.actionAsignarProductoFragmentToMessageBottomSheet(
                        "Error al asignarle los tallos al operario seleccionado." +
                                "\nMotivo: ${result.exception.message.toString()}"
                    ))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    private fun limpiarCampos() {
        edit_text_cantidad.text!!.clear()
        textInputLayout_cantidad.isErrorEnabled = false

    }

    private fun initSpinnerOperarioEnLinea() {
        operariosEnLinea.forEach{obj -> nombresOperariosEnLinea.add("${obj.nombre} ${obj.apellidos}")}
        nombresOperariosEnLinea.sortWith { o1, o2 -> o1.compareTo(o2)}

        spinnerOperarioEnLinea.adapter = ArrayAdapter(requireContext(),
            R.layout.custom_spinner_list_item, nombresOperariosEnLinea)
        spinnerOperarioEnLineaOnItemSelectedListener()
    }

    private fun initSpinnerTipoTallo() {
        nombresTipoTallo.add("BQT")
        nombresTipoTallo.add("ECB")
        nombresTipoTallo.add("PQT")
        nombresTipoTallo.add("CB")
        //bqt, ecb, paquete, consumer
        nombresTipoTallo.sortWith { o1, o2 -> o1.compareTo(o2)}

        spinnerTipoTallo.adapter = ArrayAdapter(requireContext(),
            R.layout.custom_spinner_list_item, nombresTipoTallo)
        spinnerTipoTalloOnItemSelectedListener()
    }

    fun spinnerTipoTalloOnItemSelectedListener(){
        spinnerTipoTallo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                tipoTalloSeleccionado = ""
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                tipoTalloSeleccionado = nombresTipoTallo[position]
            }
        }
    }

    private fun getOperariosEnLinea(){
        viewModel.getOperariosEnLinea(user).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sLoading} Obteniendo operarios en linea")
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sSuccess} Operarios en linea obtenidos: ${result.data}")
                    operariosEnLinea = result.data
                    if(operariosEnLinea.size!=0) initSpinnerOperarioEnLinea()
                }
                is Resource.Failure -> {
                    showProgressBar(false, progress_horizontal)
                    when (result.exception) {
                        is NullPointerException -> {
                            findNavController().navigate(AsignarProductoFragmentDirections.actionAsignarProductoFragmentToMessageBottomSheet
                                    ("La línea ${user.linea} de la finca ${user.finca} no tiene operarios en línea." +
                                    "\nDebe poner en línea algún operario para poder asignarle un producto" ))
                        }
                        else -> Log.w(TAG,"${MessageBuilder.sFailure} Respuesta excepcion: ${result.exception.message}")
                    }
                }
            }
        })
    }

    fun spinnerOperarioEnLineaOnItemSelectedListener(){
        spinnerOperarioEnLinea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                operarioEnLineaSeleccionado = ""
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                operarioEnLineaSeleccionado = nombresOperariosEnLinea[position]
                setTallosAsignados()
            }
        }
    }

    private fun setTallosAsignados() {
        btnVer.isClickable = false
        operariosEnLinea.forEach { obj -> if ("${obj.nombre} ${obj.apellidos}" == operarioEnLineaSeleccionado) operarioIDSeleccionado = obj.operarioId }
        operariosEnLinea.forEach { obj ->
            if ("${obj.nombre} ${obj.apellidos}" == operarioEnLineaSeleccionado)
                getTallosPorHora(obj, rendimientoPorHora, progress_horizontal)
        }
        viewModel.getStems(operarioIDSeleccionado, user).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sLoading} Obteniendo tallos")
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sSuccess} Tallos obtenidos: ${result.data}")
                    tallosAsignados = result.data
                    if(tallosAsignados.size!=0)
                        llenarUltimoProducto("${tallosAsignados[tallosAsignados.size-1].tallos} tallos",
                            "${tallosAsignados[tallosAsignados.size-1].horaInicio}")
                }
                is Resource.Failure -> {
                    showProgressBar(false, progress_horizontal)
                    tallosAsignados = ArrayList()
                    cantTallos.text = "0 tallos"
                    tv_horaInicio.text = "--:--"
                    tv_Inicio.text = "-.-."
                    btnVer.isClickable = true
                    Log.w(TAG,"${MessageBuilder.sFailure} Respuesta excepcion: ${result.exception.message}")
                }
            }
        })
    }

    private fun llenarUltimoProducto(tallos: String, horaInicio: String) {
        val contexto = Contexto()
        contexto.setHoraVeintiCuatro(horaInicio, contexto)
        cantTallos.text = tallos
        tv_horaInicio.text = contexto.hora12
        tv_Inicio.text = contexto.AMPM
        btnVer.isClickable = true
    }

    //revisar si siempre hay id operario en línea para calcular el getTallosPorHora
}