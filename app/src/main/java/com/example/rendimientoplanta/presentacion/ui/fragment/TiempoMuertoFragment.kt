package com.example.rendimientoplanta.presentacion.ui.fragment

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.pojos.Operario
import com.example.rendimientoplanta.base.utils.validators.FieldValidators
import com.example.rendimientoplanta.data.repository.TiempoMuertoRepo
import com.example.rendimientoplanta.domain.impldomain.TiempoMuertoCase
import com.example.rendimientoplanta.pattern.interprete.Contexto
import com.example.rendimientoplanta.presentacion.factory.TiempoMuertoFactory
import com.example.rendimientoplanta.presentacion.viewmodel.TiempoMuertoVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_jornada.*
import kotlinx.android.synthetic.main.fragment_tiempo_muerto.*
import kotlinx.android.synthetic.main.fragment_tiempo_muerto.btnGuardar
import kotlinx.android.synthetic.main.fragment_tiempo_muerto.cv_fin
import kotlinx.android.synthetic.main.fragment_tiempo_muerto.cv_inicio
import kotlinx.android.synthetic.main.fragment_tiempo_muerto.progress_horizontal
import kotlinx.android.synthetic.main.fragment_tiempo_muerto.tv_Fin
import kotlinx.android.synthetic.main.fragment_tiempo_muerto.tv_Inicio
import kotlinx.android.synthetic.main.fragment_tiempo_muerto.tv_horaFin
import kotlinx.android.synthetic.main.fragment_tiempo_muerto.tv_horaInicio
import java.text.SimpleDateFormat
import java.util.*

class TiempoMuertoFragment : BaseFragment(true, true, "Tiempo Muerto"){
    private val TAG = "TiempoMuertoFragment"
    override fun getViewID(): Int = R.layout.fragment_tiempo_muerto
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            TiempoMuertoFactory(TiempoMuertoCase(TiempoMuertoRepo()))
        ).get(TiempoMuertoVM::class.java)
    }
    private var operarios = ArrayList<Operario>()
    private var nombres = ArrayList<String>()
    private var seleccionado = Operario(0, "", "", "", 0, false, false, user.uid, "",false)
    var motivo = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getOperarios()

        tv_lineaInicio.text = "Línea ${user.linea}"
        tv_lineaFin.text = "Línea ${user.linea}"
        setHora(tv_horaInicio, tv_Inicio, "${SimpleDateFormat("hh").format(Date()).toInt()}:${SimpleDateFormat("mm").format(Date())}")
        setHora(tv_horaFin,tv_Fin, "${SimpleDateFormat("hh").format(Date()).toInt()}:${SimpleDateFormat("mm").format(Date())}")
        btnGuardar.setOnClickListener { if(FieldValidators.isValidate(textInputLayout_motivo, edit_text_motivo, resources, "* Campo requerido")) guardarMotivo()}
        btnVer.setOnClickListener {
            if(seleccionado.uid==0)
                findNavController().navigate(TiempoMuertoFragmentDirections.actionTiempoMuertoFragmentToMessageBottomSheet("Debe seleccionar un operario para sus tiempos muertos"))
            else
                verTiemposMuertos()
        }
        cv_inicio.setOnClickListener { openTimePicker(0, R.style.DatePickerDialogTheme_Primary) }
        cv_fin.setOnClickListener { openTimePicker(1, R.style.DatePickerDialogTheme_Green) }
    }

    private fun verTiemposMuertos() {

    }

    private fun getOperarios(){
        viewModel.getOperarios(user).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sLoading} Obteniendo operarios")
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sSuccess} Operarios obtenidos: ${result.data}")
                    operarios = result.data
                    if(operarios.size!=0) initSpinnerOperarios()
                }
                is Resource.Failure -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG,"${MessageBuilder.sFailure} Respuesta excepcion: ${result.exception.message}")
                }
            }
        })
    }

    private fun initSpinnerOperarios() {
        operarios.forEach{obj -> nombres.add("${obj.nombre} ${obj.apellidos}")}
        spinnerOperario.adapter = ArrayAdapter(requireContext(),
            R.layout.custom_spinner_list_item, nombres)
        spinnerOperariosOnItemSelectedListener()
    }

    private fun spinnerOperariosOnItemSelectedListener() {
        spinnerOperario.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                seleccionado = Operario(0, "", "", "", 0, false, false, user.uid, "",false)
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                operarios.forEach { obj -> if("${obj.nombre} ${obj.apellidos}" == nombres[position])  seleccionado = obj}
                Log.e(TAG, "Operario seleccionado: $seleccionado")
            }
        }
    }

    private fun guardarMotivo() {
        if(seleccionado.uid==0)
            findNavController().navigate(TiempoMuertoFragmentDirections.actionTiempoMuertoFragmentToMessageBottomSheet("Debe seleccionar un operario para aplicar un tiempo muerto"))
        else
            guardarTiempoMuerto()
    }

    private fun guardarTiempoMuerto() {
        viewModel.setTiempoMuerto(seleccionado, user, "${tv_horaInicio.text} ${tv_Inicio.text}", "${tv_horaFin.text} ${tv_Fin.text}", edit_text_motivo.text.toString()).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sLoading} Guardando tiempo muerto")
                }
                is Resource.Success -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sSuccess} ${result.data}")
                    findNavController().navigate(TiempoMuertoFragmentDirections.actionTiempoMuertoFragmentToMessageBottomSheet(result.data))

                }
                is Resource.Failure -> {
                    showProgressBar(false, progress_horizontal)
                    Log.w(TAG,"${MessageBuilder.sFailure} Respuesta excepcion: ${result.exception.message}")
                }
            }
        })
    }

    private fun openTimePicker(time: Int, datepickerDialogTheme: Int) {
        val mCurrentTime: Calendar = Calendar.getInstance()
        val hour: Int = mCurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute: Int = mCurrentTime.get(Calendar.MINUTE)
        val mTimePicker = TimePickerDialog(
            requireContext(), datepickerDialogTheme,
            { _, selectedHour, selectedMinute ->
                if (time == 0)
                    setHora(tv_horaInicio, tv_Inicio, "$selectedHour:$selectedMinute")
                else
                    setHora(tv_horaFin, tv_Fin, "$selectedHour:$selectedMinute")

            },
            hour,
            minute,
            false
        ) //Yes 24 hour time
        mTimePicker.show()
    }

    private fun setHora(tv_hora: TextView, tv_tiempo: TextView, time: String) {
        val contexto = Contexto()
        contexto.setHoraVeintiCuatro("${Integer.parseInt(time.split(":")[0])}:${Integer.parseInt(time.split(":")[1])}", contexto)
        tv_hora.text = contexto.hora12
        tv_tiempo.text = contexto.AMPM
    }
}