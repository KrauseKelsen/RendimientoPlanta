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
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.utils.validators.FieldDecimalValidators
import com.example.rendimientoplanta.base.utils.validators.FieldValidators
import com.example.rendimientoplanta.data.repository.DesasignarProductoRepo
import com.example.rendimientoplanta.domain.impldomain.DesasignarProductoCase
import com.example.rendimientoplanta.pattern.interprete.Contexto
import com.example.rendimientoplanta.presentacion.factory.DesasignarProductoFactory
import com.example.rendimientoplanta.presentacion.viewmodel.DesasignarProductoVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_desasignar_producto.*
import kotlinx.android.synthetic.main.fragment_desasignar_producto.btnGuardar
import kotlinx.android.synthetic.main.fragment_desasignar_producto.edit_text_cantidad
import kotlinx.android.synthetic.main.fragment_desasignar_producto.edit_text_motivo
import kotlinx.android.synthetic.main.fragment_desasignar_producto.progress_horizontal
import kotlinx.android.synthetic.main.fragment_desasignar_producto.rendimientoPorHora
import kotlinx.android.synthetic.main.fragment_desasignar_producto.textInputLayout_cantidad
import kotlinx.android.synthetic.main.fragment_desasignar_producto.textInputLayout_motivo
import kotlinx.android.synthetic.main.fragment_desasignar_producto.tv_Inicio
import kotlinx.android.synthetic.main.fragment_desasignar_producto.tv_horaInicio
import kotlinx.android.synthetic.main.fragment_sacar_linea.*

class DesasignarProductoFragment : BaseFragment(true, true, "Desasignar Producto"){
    private val TAG = "DesasignarProductoFragment"

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            DesasignarProductoFactory(DesasignarProductoCase(DesasignarProductoRepo()))
        ).get(DesasignarProductoVM::class.java)
    }

    private var operariosEnLinea = ArrayList<OperarioLinea>()
    private var tallosAsignados = ArrayList<TallosAsignados>()
    private var nombresOperariosEnLinea = ArrayList<String>()
    private var operarioEnLineaSeleccionado = ""
    private var operarioIDSeleccionado = 0

    override fun getViewID():Int = R.layout.fragment_desasignar_producto

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initSpinnerOperarioEnLinea()
        FieldValidators.setupListeners(textInputLayout_motivo, edit_text_motivo, resources,"Campo requerido *")
        getOperariosEnLinea()
        btnGuardar.setOnClickListener {
            if(operarioEnLineaSeleccionado!=""){
                if (tallosAsignados.size == 0) {
                    findNavController().navigate(
                        DesasignarProductoFragmentDirections.actionDesasignarProductoFragmentToMessageBottomSheet
                            ("El operario no tiene tallos asignados, asignele tallos e intente nuevamente"))
                }else{
                    if(FieldDecimalValidators.isValidate(textInputLayout_cantidad, edit_text_cantidad, resources, tallosAsignados[0].tallos.toDouble())
                        && FieldValidators.isValidate(textInputLayout_motivo, edit_text_motivo, resources, "Campo requerido *")) {
                        desasignarTallos()
                    }
                }
            }else{
                findNavController().navigate(DesasignarProductoFragmentDirections.actionDesasignarProductoFragmentToMessageBottomSheet("Debe seleccionar un operario para desasignar los tallos"))
            }
        }
    }

    private fun desasignarTallos() {
        lateinit var operarioEnLinea : OperarioLinea
        operariosEnLinea.forEach{obj -> operarioEnLinea = obj}
        desasignarTallos(operarioEnLinea)

    }

    private fun desasignarTallos(operarioEnLinea: OperarioLinea) {
        val tallosAsignadosInicialmente = tallosAsignados[tallosAsignados.size-1].tallos
        viewModel.setStems(operarioEnLinea, user, edit_text_cantidad.text.toString().toInt(), edit_text_motivo.text.toString(), tallosAsignados).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sLoading} Desasignando tallos")
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sSuccess} Tallos desasignados: ${result.data}")
                    findNavController().navigate(
                        DesasignarProductoFragmentDirections.actionDesasignarProductoFragmentToMessageBottomSheet
                            ("Han sido desasignados ${tallosAsignadosInicialmente-edit_text_cantidad.text.toString().toInt()} tallos al operario $operarioEnLineaSeleccionado"))
                    setTallosAsignados()
                    limpiarCampos()
                }
                is Resource.Failure -> {
                    findNavController().navigate(DesasignarProductoFragmentDirections.actionDesasignarProductoFragmentToMessageBottomSheet(
                        "Error al desasignar los tallos." +
                                "\nMotivo: ${result.exception.message.toString()}"
                    ))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    private fun limpiarCampos() {
        edit_text_motivo.text!!.clear()
        edit_text_cantidad.text!!.clear()
        edit_text_cantidad.isFocusable = false
        edit_text_motivo.isFocusable = false
        textInputLayout_cantidad.isErrorEnabled = false
        textInputLayout_motivo.isErrorEnabled = false
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
                            findNavController().navigate(DesasignarProductoFragmentDirections.actionDesasignarProductoFragmentToMessageBottomSheet
                                ("La línea ${user.linea} de la finca ${user.finca} no tiene operarios en línea con producto asignado." +
                                    "\nDebe poner en línea algún operario y asignarle un producto." ))
                        }
                        else -> {
                            findNavController().navigate(DesasignarProductoFragmentDirections.actionDesasignarProductoFragmentToMessageBottomSheet
                                ("Error en la validación de obtener los operarios en línea con producto asignado." ))
                            Log.w(TAG,"${MessageBuilder.sFailure} Respuesta excepcion: ${result.exception.message}")
                        }
                    }
                }
            }
        })
    }

    private fun initSpinnerOperarioEnLinea() {
        operariosEnLinea.forEach{obj -> nombresOperariosEnLinea.add("${obj.nombre} ${obj.apellidos}")}
        nombresOperariosEnLinea.sortWith { o1, o2 -> o1.compareTo(o2)}

        spinnerOperarioEnLinea.adapter = ArrayAdapter(requireContext(),
            R.layout.custom_spinner_list_item, nombresOperariosEnLinea)
        spinnerOperarioEnLineaOnItemSelectedListener()
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
                    if(tallosAsignados.size!=0) llenarUltimoProducto("${tallosAsignados[tallosAsignados.size-1].tallos} tallos", "${tallosAsignados[tallosAsignados.size-1].horaInicio}")
                }
                is Resource.Failure -> {
                    showProgressBar(false, progress_horizontal)
                    tallosAsignados = ArrayList()
                    cantTallos.text = "0 tallos"
                    tv_horaInicio.text = "--:--"
                    tv_Inicio.text = "-.-."
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
    }
}