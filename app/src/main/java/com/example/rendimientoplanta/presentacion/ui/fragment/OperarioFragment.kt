package com.example.rendimientoplanta.presentacion.ui.fragment

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.pojos.ArrayListTallosAsignados
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.OperarioLinea_TallosAsignados
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.utils.validators.FieldCredentialValidators.isNumeric
import com.example.rendimientoplanta.data.repository.OperarioRepo
import com.example.rendimientoplanta.domain.impldomain.OperarioCase
import com.example.rendimientoplanta.presentacion.adapter.AdapterOperariosEnLinea
import com.example.rendimientoplanta.presentacion.adapter.SwipeAdapterSacarLinea
import com.example.rendimientoplanta.presentacion.factory.OperarioFactory
import com.example.rendimientoplanta.presentacion.ui.dialogs.DialogMessage
import com.example.rendimientoplanta.presentacion.viewmodel.OperarioVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_operario.*

@RequiresApi(Build.VERSION_CODES.M)
class OperarioFragment : BaseFragment(true, true, "Operario") , AdapterOperariosEnLinea.OnOperariosClickListenerTrash, DialogMessage.FinalizoDialog{
    private val TAG = "OperarioFragment"
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            OperarioFactory(OperarioCase(OperarioRepo()))
        ).get(OperarioVM::class.java)
    }

    private lateinit var adapterOperariosEnLinea: AdapterOperariosEnLinea
    private lateinit var operariosSaved: ArrayList<OperarioLinea>
    private lateinit var tallosAsignadosSaved: ArrayList<TallosAsignados>
    private var codigoOperario: Int = 0
    private lateinit var operario: OperarioLinea

    override fun getViewID():Int = R.layout.fragment_operario

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

        recyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
            visibleKeyboardOrFloatingButtom(false, true, "Toque el recycler")
        }

        btnKey.setOnClickListener {
            visibleKeyboardOrFloatingButtom(true, false, "Toque el floatingbutton")
        }

        btnAdd.setOnClickListener {
            btnAddPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        visibleKeyboardOrFloatingButtom(false, true, "Toque el recycler")
        getOperariosIngresados()
        setOnClickListenerBtns()
        labelCodigo_textChange()
        setValues()
    }

    private fun labelCodigo_textChange() {
        labelCodigo.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(isNumeric(s.toString())){
                    if(s.toString().length<4)
                        setValues("Ingresa un código válido", 0)
                    else
                        getOperario(s.toString().toInt())
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun getOperario(codigo: Int){
        viewModel.getOperario(codigo).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Obteniendo operario...")
                    setValues("Buscando operario...", 0)
                }
                is Resource.Success -> {
                    Log.w(TAG, "Operario obtenido")
                    if(!result.data.estado){
                        findNavController().navigate(OperarioFragmentDirections
                            .actionOperarioFragmentToMessageBottomSheet("El operario ${result.data.nombre} ${result.data.apellidos} está deshabilitado, solicite al administrador que lo habilite" +
                                    " para que posteriormente sea puesto en línea."))
                        setValues("Ingresa otro operario", 0)
                    }else{
                        Log.w(TAG, "Operario obtenido: ${result.data.nombre} ${result.data.apellidos}")
                        setValues("${result.data.nombre} ${result.data.apellidos}", result.data.codigo)
                    }
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure -> {
                    when(result.exception){
                        is NumberFormatException ->{
                            setValues("Ingresa un código válido", 0)
                        }
                        else -> findNavController().navigate(OperarioFragmentDirections
                            .actionOperarioFragmentToMessageBottomSheet("Error al cargar el operario ingresado. \nMotivo: ${result.exception.message.toString()}"))
                    }
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    fun setValues(s: String, i: Int){
        labelNombre.text = s
        codigoOperario = i
    }

    fun setValues(){
        labelNombre.text = "Ingrese un código"
        codigoOperario = 0
        labelCodigo.text = ""
    }

    fun setAdapter(){
        adapterOperariosEnLinea = AdapterOperariosEnLinea(requireContext(), this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapterOperariosEnLinea
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun getOperariosIngresados() {
        setAdapter()
        viewModel.getOperariosIngresados(user).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Obteniendo operarios ingresados...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Operarios ingresados obtenidos")
                    operariosSaved = result.data
                    getStems()
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure -> {
                    showProgressBar(false, progress_horizontal)
                    findNavController().navigate(OperarioFragmentDirections
                        .actionOperarioFragmentToMessageBottomSheet("Error al cargar los operarios en línea. \nMotivo: ${result.exception.message.toString()}"))
                }
            }
        })
        setValues()
    }

    private fun setRecyclerView() {
        tv_cantOperarios.text = "${operariosSaved.size} Operarios ingresados"
        if(operariosSaved.size!=0){
            val swipe = object : SwipeAdapterSacarLinea(requireContext()){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    when(direction) {
                        ItemTouchHelper.LEFT -> {
                            Log.w(TAG, "Operario ${operariosSaved[viewHolder.adapterPosition].nombre} obtenido en el Swipe LEFT")
                            updTallosAsignados(operariosSaved[viewHolder.adapterPosition])
                        }
                        ItemTouchHelper.RIGHT -> {
                            Log.w(TAG, "Operario ${operariosSaved[viewHolder.adapterPosition].nombre} obtenido en el Swipe RIGHT")
                            updTallosAsignados(operariosSaved[viewHolder.adapterPosition])
                        }
                    }
                }
            }
            ItemTouchHelper(swipe).attachToRecyclerView(recyclerView)
        }
        adapterOperariosEnLinea.setListData(armarOperariosXTallos())
        adapterOperariosEnLinea.notifyDataSetChanged()
    }

    private fun armarOperariosXTallos(): ArrayList<OperarioLinea_TallosAsignados> {
        var operariosLaburando = 0
        var operariosLibres = 0
        val array = ArrayList<OperarioLinea_TallosAsignados>()
        operariosSaved.forEach { operarioLinea -> array.add(OperarioLinea_TallosAsignados(operarioLinea, false)) }
        for (obj in array) {
            for (obj2 in tallosAsignadosSaved) {
                if(obj.operarioLinea.operarioId==obj2.operarioId) {
                    obj.tallosAsignados = true
                }
            }
        }
        array.forEach { obj -> if(obj.tallosAsignados) operariosLaburando++ else operariosLibres++ }
        if(operariosLaburando==1) laburando.text = "$operariosLaburando operario empacando" else laburando.text = "$operariosLaburando operarios empacando"
        if(operariosLibres==1) libres.text = "$operariosLibres operario en línea" else libres.text = "$operariosLibres operarios en línea"


        return array
    }

    override fun onItemClickTrash(obj: OperarioLinea) {
        operario = obj
        var bandera = false
        for (res in armarOperariosXTallos()) {
            if (res.tallosAsignados && res.operarioLinea.operarioId == operario.operarioId) {
                bandera = true
            }
        }
        var arrayTallos = ArrayListTallosAsignados(ArrayList())
        if(bandera){
            for(tallos in tallosAsignadosSaved){
                if(operario.operarioId == tallos.operarioId){
                    arrayTallos.arrayList.add(tallos)
                }
            }

            findNavController().navigate(OperarioFragmentDirections.actionOperarioFragmentToSacarLineaFragment(operario, arrayTallos))
        }else{
            DialogMessage(context, this, "El operario no tiene tallos asignados, ¿desea sacarlo de la línea?")
        }
    }

    fun updTallosAsignados(obj: OperarioLinea){
        viewModel.updtallosAsignados(obj).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Obteniendo tallos ingresados...")
                }
                is Resource.Success -> {
                    findNavController().navigate(OperarioFragmentDirections.actionOperarioFragmentToMessageBottomSheet(result.data))
                    getOperariosIngresados()
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure -> {
                    findNavController().navigate(OperarioFragmentDirections.actionOperarioFragmentToMessageBottomSheet("Error al finalizar los tallos asignados del operario." +
                            "\nMotivo: ${result.exception.message.toString()}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    fun getStems(){
        viewModel.getStems().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Obteniendo tallos ingresados...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Tallos obtenidos ${result.data}")
                    tallosAsignadosSaved = result.data
                    setRecyclerView()
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure -> {
                    findNavController().navigate(OperarioFragmentDirections
                        .actionOperarioFragmentToMessageBottomSheet("Error al obtener los tallos asignados.\nMotivo: ${result.exception.message.toString()}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    private fun delPressed() {
        if (labelCodigo.text.toString() != "")
            labelCodigo.text = labelCodigo.text.toString().substring(0,labelCodigo.text.toString().length - 1)
        else
            visibleKeyboardOrFloatingButtom(false, true, "Toque el recycler")
    }

    private fun numberPressed(num: String){
        if (labelCodigo.text.length>=4)
            delPressed()
        labelCodigo.text = "${labelCodigo.text}$num"

    }

    private fun btnAddPressed(){
        if (codigoOperario != 0) {
            getOperarioLinea(codigoOperario)
        }
    }

    fun getOperarioLinea(codigoOperario: Int) {
        viewModel.getOperarioLinea(codigoOperario).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Obteniendo operario en linea...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Operario obtenido ${result.data}")
                    findNavController().navigate(OperarioFragmentDirections.actionOperarioFragmentToMessageBottomSheet("El código de operario ya está ingresado, " +
                            "en la finca ${result.data.finca} y la línea ${result.data.lineaId} debe sacarlo de la línea si desea volverlo a ingresar"))
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure -> {
                    when(result.exception) {
                        is NullPointerException -> setOperarioLinea(codigoOperario)
                        else -> findNavController().navigate(OperarioFragmentDirections
                            .actionOperarioFragmentToMessageBottomSheet("Error al verificar si el operario se encuentra en línea. \nMotivo: ${result.exception.message.toString()}"))
                    }
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
        setValues()
    }

    fun setOperarioLinea(codigoOperario: Int) {
        viewModel.setOperarioEnLinea(codigoOperario, user).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Ingresando operario...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Operario ingresado ${result.data}")
                    getOperariosIngresados()
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure -> {
                    findNavController().navigate(OperarioFragmentDirections
                        .actionOperarioFragmentToMessageBottomSheet("Error al ingresar el operario a la línea. \nMotivo: ${result.exception.message.toString()}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
        setValues()
    }

    override fun resultadoYesDialog(dialog: Dialog?) {
        dialog?.dismiss()
        viewModel.updOperarioLinea(operario).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Inactivando operario...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Operario inactivado ${result.data}")
                    getOperariosIngresados()
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure -> {
                    findNavController().navigate(OperarioFragmentDirections
                        .actionOperarioFragmentToMessageBottomSheet("Error al sacar el operario seleccionado de la línea. \nMotivo: ${result.exception.message.toString()}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
        setValues()
    }

    override fun resultadoNoDialog(dialog: Dialog?) {
        dialog?.dismiss()
    }
}