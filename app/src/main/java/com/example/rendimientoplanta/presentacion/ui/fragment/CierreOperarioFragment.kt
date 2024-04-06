package com.example.rendimientoplanta.presentacion.ui.fragment

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.pojos.Receso
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.utils.validators.CalcuTimeValidators
import com.example.rendimientoplanta.base.utils.validators.FieldCredentialValidators
import com.example.rendimientoplanta.data.repository.CierreOperarioRepo
import com.example.rendimientoplanta.domain.impldomain.CierreOperarioCase
import com.example.rendimientoplanta.presentacion.factory.CierreOperarioFactory
import com.example.rendimientoplanta.presentacion.viewmodel.CierreOperarioVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_cierre_operario.*
import kotlinx.android.synthetic.main.fragment_cierre_operario.btn0
import kotlinx.android.synthetic.main.fragment_cierre_operario.btn1
import kotlinx.android.synthetic.main.fragment_cierre_operario.btn2
import kotlinx.android.synthetic.main.fragment_cierre_operario.btn3
import kotlinx.android.synthetic.main.fragment_cierre_operario.btn4
import kotlinx.android.synthetic.main.fragment_cierre_operario.btn5
import kotlinx.android.synthetic.main.fragment_cierre_operario.btn6
import kotlinx.android.synthetic.main.fragment_cierre_operario.btn7
import kotlinx.android.synthetic.main.fragment_cierre_operario.btn8
import kotlinx.android.synthetic.main.fragment_cierre_operario.btn9
import kotlinx.android.synthetic.main.fragment_cierre_operario.btnAdd
import kotlinx.android.synthetic.main.fragment_cierre_operario.btnDel
import kotlinx.android.synthetic.main.fragment_cierre_operario.btnKey
import kotlinx.android.synthetic.main.fragment_cierre_operario.labelCodigo
import kotlinx.android.synthetic.main.fragment_cierre_operario.teclado
import kotlinx.android.synthetic.main.fragment_cierre_operario.linearLayoutFloatingActionButton
import kotlinx.android.synthetic.main.fragment_cierre_operario.progress_horizontal
import kotlinx.android.synthetic.main.fragment_cierre_operario.spinnerOperarioEnLinea
import java.lang.NumberFormatException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class CierreOperarioFragment : BaseFragment(true, true, "Cierre de Operario"){
    private val TAG = "CierreOperarioFragment"
    private var tallosAsignadosAbiertos = ArrayList<TallosAsignados>()
    private var tallosAsignadosCerrados = ArrayList<TallosAsignados>()
    private var recesos = ArrayList<Receso>()
    private var nombreOperariosEnLinea = ArrayList<String>()
    private var nombreOperariosEnLineaHashSet = HashSet<String>()
    private var seleccionado = TallosAsignados("",0,0,"","","",0,"",0,"","","",false,"","",false,false, false)
    private var cierreOperario = false
    private var seleccionadoBoolean = false

    private var tallosAsignadosText = 0
    private var tallosAlCierreText = 0
    private var tallosPorHoraText = BigDecimal(0)
    private var efectividadText = "00 h 00 min"
    private var porcentaje_rendimientoText = "0%"

    override fun getViewID():Int = R.layout.fragment_cierre_operario
    private val args: CierreOperarioFragmentArgs by navArgs()
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            CierreOperarioFactory(CierreOperarioCase(CierreOperarioRepo()))
        ).get(CierreOperarioVM::class.java)
    }

    fun visibleKeyboardOrFloatingButtom(keyboard: Boolean, floating: Boolean, msj: String){
        Log.w(TAG, msj)
        teclado.isVisible = keyboard
        linearLayoutFloatingActionButton.isVisible = floating
    }

    fun setOnClickListenerBtns(){
        btn1.setOnClickListener { numberPressed("1") }
        btn2.setOnClickListener { numberPressed("2") }
        btn3.setOnClickListener { numberPressed("3") }
        btn4.setOnClickListener { numberPressed("4") }
        btn5.setOnClickListener { numberPressed("5") }
        btn6.setOnClickListener { numberPressed("6") }
        btn7.setOnClickListener { numberPressed("7") }
        btn8.setOnClickListener { numberPressed("8") }
        btn9.setOnClickListener { numberPressed("9") }
        btn0.setOnClickListener { numberPressed("0") }
        btnDel.setOnClickListener { delPressed() }
        teclado.isVisible = false
        linearLayout.setOnClickListener {
            visibleKeyboardOrFloatingButtom(false, true, "Toque el linearLayout")
        }

        btnKey.setOnClickListener {
            if(cierreOperario)
                findNavController().navigate(CierreOperarioFragmentDirections.actionCierreOperarioFragmentToMessageBottomSheet(
                    "El operario ${seleccionado.nombreOperario} ${seleccionado.apellidosOperario} ya posee un cierre de operario en el cierre de línea actual"))
            else{
                if(!seleccionadoBoolean)
                    addPressed(0)
                else
                    visibleKeyboardOrFloatingButtom(true, false, "Toque el floatingbutton")
            }
        }

        btnAdd.setOnClickListener { addPressed() }
        labelCodigo_textChange()

    }

    private fun addPressed() {
        addPressed(labelCodigo.text.toString().toInt())
    }

    private fun addPressed(cantidadTallos: Int) {
        val tallosBeforeHour = CalcuTimeValidators.getTallosBeforeHourOfOperario(tallosAsignadosCerrados, args.cierreLinea!!.horaInicio, seleccionado.operarioId)
        if(seleccionadoBoolean){
            val muletilla = TallosAsignados(seleccionado.uid, seleccionado.fincaId, seleccionado.lineaId, seleccionado.fecha, seleccionado.horaInicio,
                SimpleDateFormat("HH:mm").format(Date()), cantidadTallos, seleccionado.tipo, seleccionado.operarioId, seleccionado.operarioLineaId, seleccionado.nombreOperario,
                seleccionado.apellidosOperario, false, seleccionado.fechaCreacion, seleccionado.user, seleccionado.primero, seleccionado.segundo, seleccionado.sql)
            tallosBeforeHour.add(muletilla)
        }

        viewModel.putCierreOperario(user, cantidadTallos, tallosBeforeHour, seleccionado.operarioId,
            args.cierreLinea!!, recesos, rendimiento).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sLoading} Registrando cierre de operario")
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sSuccess} ${result.data}")
                    cierreOperario = true
                    findNavController().navigate(CierreOperarioFragmentDirections.actionCierreOperarioFragmentToMessageBottomSheet(
                        "Registro de cierre de operario realizado exitosamente"))
                }
                is Resource.Failure -> {
                    showProgressBar(false, progress_horizontal)
                    findNavController().navigate(CierreOperarioFragmentDirections.actionCierreOperarioFragmentToMessageBottomSheet(
                        "Error al registrar el cierre de operario. \nMotivo: ${result.exception.message}"))
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e(TAG, args.cierreLinea!!.horaInicio)
        setOnClickListenerBtns()

        //TRAER LOS RECESOS
        //TRAERSE TODOS LOS TALLOS DEL OPERARIO SELECCIONADO
        //FINALMENTE TAERSE EN OTRO ARREGLO SOLO LOS TALLOS SIN CERRAR CON getOperariosEnLina()

        getOperariosEnLineaUndTallosAbiertos()
    }

    private fun getOperariosEnLineaUndTallosAbiertos(){
        viewModel.getTallosAsignados(args.cierreLinea!!, true).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sLoading} Obteniendo tallos asignados")
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sSuccess} Tallos asignados obtenidos: ${result.data}")
                    tallosAsignadosAbiertos = result.data
                    //if(tallosAsignadosAbiertos.size!=0) initSpinnerOperarioEnLinea()
                    getTallosCerrados()
                }
                is Resource.Failure -> {
                    showProgressBar(false, progress_horizontal)
                    findNavController().navigate(CierreOperarioFragmentDirections.actionCierreOperarioFragmentToMessageBottomSheet(
                        "Error al cargar los tallos abiertos. \nMotivo: ${result.exception.message}"))
                }
            }
        })
    }

    private fun getTallosCerrados() {
        viewModel.getTallosAsignados(args.cierreLinea!!, false).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sLoading} Obteniendo tallos asignados")
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sSuccess} Tallos asignados obtenidos: ${result.data}")
                    tallosAsignadosCerrados = result.data
                    //calcularCard()
                    initSpinnerOperarioEnLinea()
                }
                is Resource.Failure -> {
                    showProgressBar(false, progress_horizontal)
                    findNavController().navigate(CierreOperarioFragmentDirections.actionCierreOperarioFragmentToMessageBottomSheet(
                        "Error al cargar los tallos cerrados. \nMotivo: ${result.exception.message}"))
                }
            }
        })
    }

    private fun initSpinnerOperarioEnLinea() {
        tallosAsignadosAbiertos.forEach{obj -> nombreOperariosEnLineaHashSet.add("${obj.nombreOperario} ${obj.apellidosOperario}")}
        tallosAsignadosCerrados.forEach{obj -> nombreOperariosEnLineaHashSet.add("${obj.nombreOperario} ${obj.apellidosOperario}")}
        for(hash in nombreOperariosEnLineaHashSet)
            nombreOperariosEnLinea.add(hash)

        nombreOperariosEnLinea.sortWith { o1, o2 -> o1.compareTo(o2)}

        spinnerOperarioEnLinea.adapter = ArrayAdapter(requireContext(),
            R.layout.custom_spinner_list_item, nombreOperariosEnLinea)
        spinnerOperarioEnLineaOnItemSelectedListener()
    }

    private fun spinnerOperarioEnLineaOnItemSelectedListener() {
        spinnerOperarioEnLinea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                seleccionado = TallosAsignados("",0,0,"","","",0,"",0,"","","",false,"","",false,false, false)
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                seleccionado = TallosAsignados("",0,0,"","","",0,"",0,"","","",false,"","",false,false, false)
                resetValues(0, 0, BigDecimal(0),"",  "0%")
                cierreOperario = false
                seleccionadoBoolean = false

                if(tallosAsignadosAbiertos.size!=0) tallosAsignadosAbiertos.reverse()
                for(obj in tallosAsignadosAbiertos){
                    if("${obj.nombreOperario} ${obj.apellidosOperario}" == nombreOperariosEnLinea[position]){
                        seleccionado = obj
                        seleccionadoBoolean = true
                    }
                }

                if(!seleccionadoBoolean){
                    for(obj in tallosAsignadosCerrados){
                        if("${obj.nombreOperario} ${obj.apellidosOperario}" == nombreOperariosEnLinea[position]){
                            seleccionado = obj
                        }
                    }
                }
                Log.e(TAG, "Operario seleccionado: $seleccionado")
                //VALIDAR SI YA EXISTE EL CIERRE DE OPERARIO DE ESTE OPERARIO SELECCIONADO Y CARGARLE LOS DATOS, SI NO EXISTE ENTONCES LE CARGO LOS RECESOS
                getCierreOperario()
                operarioEnLinea.text = "${seleccionado.nombreOperario} ${seleccionado.apellidosOperario}"
            }
        }
    }

    fun getCierreOperario(){
        viewModel.getCierreOperario(args.cierreLinea!!, seleccionado.operarioId).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sLoading} Obteniendo cierreOperario")
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sSuccess} CierreOperario obtenido: ${result.data}")
                    cierreOperario = true

                    var tallosPar = 0

                    tallosAsignadosAbiertos.forEach { tallosAsignados -> if(tallosAsignados.operarioId == result.data.operarioId) tallosPar = tallosAsignados.tallos}
                    tallosAsignadosCerrados.forEach { tallosAsignados -> if(tallosAsignados.operarioId == result.data.operarioId) tallosPar += tallosAsignados.tallos}

                    resetValues(tallosPar, (result.data.tallosParciales+result.data.tallosCompletados),
                        BigDecimal(result.data.tallosXhora), CalcuTimeValidators.getEfectividad(result.data.minutosEfectivos.toDouble()),
                        "${result.data.rendimientoXhora.toInt()}%")
                }
                is Resource.Failure -> {
                    when(result.exception){
                        is NumberFormatException -> getRecesos()
                        else -> {
                            findNavController().navigate(CierreOperarioFragmentDirections.actionCierreOperarioFragmentToMessageBottomSheet(
                                "Error al cargar los cierres de operario. \nMotivo: ${result.exception.message}"
                            ))
                        }
                    }
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    fun resetValues(ptallosAsignadosText: Int, ptallosAlCierreText: Int, ptallosPorHoraText: BigDecimal, pefectividadText: String, pporcentaje_rendimientoText: String) {
        tallosAsignadosText = ptallosAsignadosText
        tallosAlCierreText = ptallosAlCierreText
        tallosPorHoraText = ptallosPorHoraText
        efectividadText = pefectividadText
        porcentaje_rendimientoText = pporcentaje_rendimientoText
        llenarCard()
    }
    fun llenarCard(){
        tallosAsignados.text = "Tallos asignados: $tallosAsignadosText"
        tallosAlCierre.text = "Tallos al cierre: $tallosAlCierreText"
        tallosPorHora.text = "$tallosPorHoraText tallos por hora"
        efectividad.text = "Efectividad: $efectividadText"
        porcentaje_rendimiento.text = "$porcentaje_rendimientoText"
    }

    private fun getRecesos(){
        viewModel.getRecesos(user).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sLoading} Obteniendo recesos")
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sSuccess} Recesos obtenidos: ${result.data}")
                    recesos = result.data
                    calcularCard()
                }
                is Resource.Failure -> {
                    showProgressBar(false, progress_horizontal)
                    findNavController().navigate(CierreOperarioFragmentDirections.actionCierreOperarioFragmentToMessageBottomSheet(
                        "Error al cargar los recesos. \nMotivo: ${result.exception.message}"))
                }
            }
        })
    }

    private fun calcularCard(){
        //tallos cerrados antes de la hora de cierre de linea
        val tallosBeforeHour = CalcuTimeValidators.getTallosBeforeHourOfOperario(tallosAsignadosCerrados, args.cierreLinea!!.horaInicio, seleccionado.operarioId)
        recesos = CalcuTimeValidators.getRecesosValidos(recesos, tallosBeforeHour)

        if(seleccionadoBoolean){
            resetValues(CalcuTimeValidators.getCantTallosAsignados(tallosBeforeHour, seleccionado.operarioId)+seleccionado.tallos,
                CalcuTimeValidators.getCantTallosAsignados(tallosBeforeHour, seleccionado.operarioId),
                CalcuTimeValidators.getTallosXHora(tallosBeforeHour, recesos, seleccionado.operarioId),
                CalcuTimeValidators.getEfectividad(CalcuTimeValidators.getEfectividad(tallosBeforeHour, recesos, seleccionado.operarioId)),
                CalcuTimeValidators.getRendimiento(tallosBeforeHour, recesos, rendimiento, seleccionado.operarioId))
        }else{ //el operario seleccionado no tiene tallos asignados abiertos
            resetValues(CalcuTimeValidators.getCantTallosAsignados(tallosBeforeHour, seleccionado.operarioId),
                CalcuTimeValidators.getCantTallosAsignados(tallosBeforeHour, seleccionado.operarioId),
                CalcuTimeValidators.getTallosXHora(tallosBeforeHour, recesos, seleccionado.operarioId),
                CalcuTimeValidators.getEfectividad(CalcuTimeValidators.getEfectividad(tallosBeforeHour, recesos, seleccionado.operarioId)),
                CalcuTimeValidators.getRendimiento(tallosBeforeHour, recesos, rendimiento, seleccionado.operarioId))
        }

    }

    private fun labelCodigo_textChange() {
        labelCodigo.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val tallosBeforeHour = CalcuTimeValidators.getTallosBeforeHourOfOperario(tallosAsignadosCerrados, args.cierreLinea!!.horaInicio, seleccionado.operarioId)
                if(FieldCredentialValidators.isNumeric(s.toString())){
                    if(s.toString().toInt()>seleccionado.tallos){
                        labelCodigo.text = ""
                        resetValues(CalcuTimeValidators.getCantTallosAsignados(tallosBeforeHour, seleccionado.operarioId)+seleccionado.tallos,
                            CalcuTimeValidators.getCantTallosAsignados(tallosBeforeHour, seleccionado.operarioId),
                            CalcuTimeValidators.getTallosXHora(tallosBeforeHour, recesos, seleccionado.operarioId),
                            CalcuTimeValidators.getEfectividad(CalcuTimeValidators.getEfectividad(tallosBeforeHour, recesos, seleccionado.operarioId)),
                            CalcuTimeValidators.getRendimiento(tallosBeforeHour, recesos, rendimiento, seleccionado.operarioId))
                        findNavController().navigate(CierreOperarioFragmentDirections.actionCierreOperarioFragmentToMessageBottomSheet(
                            "La cantidad de tallos debe ser menor o igual a ${seleccionado.tallos}"))
                    }else{
                        cambiarTarjeta(seleccionado, tallosBeforeHour, s.toString().toInt())
                    }
                }

                if(s.toString()==""){
                    resetValues(CalcuTimeValidators.getCantTallosAsignados(tallosBeforeHour, seleccionado.operarioId)+seleccionado.tallos,
                        CalcuTimeValidators.getCantTallosAsignados(tallosBeforeHour, seleccionado.operarioId),
                        CalcuTimeValidators.getTallosXHora(tallosBeforeHour, recesos, seleccionado.operarioId),
                        CalcuTimeValidators.getEfectividad(CalcuTimeValidators.getEfectividad(tallosBeforeHour, recesos, seleccionado.operarioId)),
                        CalcuTimeValidators.getRendimiento(tallosBeforeHour, recesos, rendimiento, seleccionado.operarioId))
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun cambiarTarjeta(seleccionado: TallosAsignados, tallosBeforeHour: ArrayList<TallosAsignados>, s: Int){
        val muletilla = TallosAsignados(seleccionado.uid, seleccionado.fincaId, seleccionado.lineaId, seleccionado.fecha, seleccionado.horaInicio,
            SimpleDateFormat("HH:mm").format(Date()), s, seleccionado.tipo, seleccionado.operarioId, seleccionado.operarioLineaId, seleccionado.nombreOperario,
            seleccionado.apellidosOperario, false, seleccionado.fechaCreacion, seleccionado.user, seleccionado.primero, seleccionado.segundo, seleccionado.sql)
        val tallosAsignadosNum = CalcuTimeValidators.getCantTallosAsignados(tallosBeforeHour, muletilla.operarioId)

        tallosBeforeHour.add(muletilla)
        resetValues(tallosAsignadosNum+seleccionado.tallos,
            tallosAsignadosNum+s,
            CalcuTimeValidators.getTallosXHora(tallosBeforeHour, recesos, muletilla.operarioId),
            CalcuTimeValidators.getEfectividad(CalcuTimeValidators.getEfectividad(tallosBeforeHour, recesos, muletilla.operarioId)),
            CalcuTimeValidators.getRendimiento(tallosBeforeHour, recesos, rendimiento, muletilla.operarioId))
    }

    private fun delPressed() {
        if (labelCodigo.text.toString() != "")
            labelCodigo.text = labelCodigo.text.toString().substring(0,labelCodigo.text.toString().length - 1)
        else
            visibleKeyboardOrFloatingButtom(false, true, "Toque el linearLayout")
    }

    private fun numberPressed(num: String){
        if (labelCodigo.text.length>=4)
            delPressed()
        labelCodigo.text = "${labelCodigo.text}$num"
    }
}