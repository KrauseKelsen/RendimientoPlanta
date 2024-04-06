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
import com.example.rendimientoplanta.base.pojos.Receso
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.data.repository.RecesoRepo
import com.example.rendimientoplanta.domain.impldomain.RecesoCase
import com.example.rendimientoplanta.pattern.interprete.Contexto
import com.example.rendimientoplanta.pattern.interprete.Expresion
import com.example.rendimientoplanta.pattern.interprete.Reloj
import com.example.rendimientoplanta.presentacion.factory.RecesoFactory
import com.example.rendimientoplanta.presentacion.viewmodel.RecesoVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_jornada.*
import kotlinx.android.synthetic.main.fragment_recesos.*
import kotlinx.android.synthetic.main.fragment_recesos.btnGuardar
import kotlinx.android.synthetic.main.fragment_recesos.cv_fin
import kotlinx.android.synthetic.main.fragment_recesos.cv_inicio
import kotlinx.android.synthetic.main.fragment_recesos.progress_horizontal
import kotlinx.android.synthetic.main.fragment_recesos.tv_Fin
import kotlinx.android.synthetic.main.fragment_recesos.tv_Inicio
import kotlinx.android.synthetic.main.fragment_recesos.tv_horaFin
import kotlinx.android.synthetic.main.fragment_recesos.tv_horaInicio
import java.text.SimpleDateFormat
import java.util.*

class RecesosFragment : BaseFragment(true, true, "Recesos"){
    private val TAG = "RecesosFragment"
    override fun getViewID(): Int = R.layout.fragment_recesos

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            RecesoFactory(RecesoCase(RecesoRepo()))
        ).get(RecesoVM::class.java)
    }    
    
    var motivo = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showProgressBar(false, progress_horizontal)
        initSpinnerMotivo()
        tv_lineaInicio.text = "Línea ${user.linea}"
        tv_lineaFin.text = "Línea ${user.linea}"
        setHora(tv_horaInicio, tv_Inicio, "${SimpleDateFormat("hh").format(Date()).toInt()}:${SimpleDateFormat("mm").format(Date())}")
        setHora(tv_horaFin,tv_Fin, "${SimpleDateFormat("hh").format(Date()).toInt()}:${SimpleDateFormat("mm").format(Date())}")
        btnGuardar.setOnClickListener { guardarMotivo() }
        btnVer.setOnClickListener { findNavController().navigate(R.id.action_recesosFragment_to_bottomSheetMotivos) }
        cv_inicio.setOnClickListener { openTimePicker(0, R.style.DatePickerDialogTheme_Primary) }
        cv_fin.setOnClickListener { openTimePicker(1, R.style.DatePickerDialogTheme_Green) }
    }

    private fun guardarMotivo() {
        if (motivo!="") {
            setReceso(user, motivo, "${tv_horaInicio.text} ${tv_Inicio.text}", "${tv_horaFin.text} ${tv_Fin.text}")
        }else{
            findNavController().navigate(RecesosFragmentDirections.actionRecesosFragmentToMessageBottomSheet("Debe ingresar un motivo para poder registrar"))
        }
    }
    
    fun setReceso(user: User, motivo: String, horaInicio: String, horaFin: String) {
        viewModel.setReceso(user, motivo, horaInicio, horaFin).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    Log.w(TAG, "${MessageBuilder.sLoading} Registrando el receso del usuario ${user.uid}")
                    showProgressBar(true, progress_horizontal)
                }
                is Resource.Success -> {
                    Log.w(TAG, "${MessageBuilder.sSuccess} Registrado en el servidor: ${result.data}")
                    findNavController().navigate(RecesosFragmentDirections.actionRecesosFragmentToMessageBottomSheet("Receso registrado exitosamente"))
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure -> {
                    findNavController().navigate(RecesosFragmentDirections.actionRecesosFragmentToMessageBottomSheet("Error al registrar el receso.\nMotivo_: ${result.exception.message}"))
                    Log.w(TAG,"${MessageBuilder.sFailure} Respuesta excepcion: ${result.exception.message}")
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    private fun initSpinnerMotivo() {
        spinnerMotivo.adapter = ArrayAdapter(requireContext(),R.layout.custom_spinner_list_item, arrayListOf("Desayuno","Almuerzo","Café tarde","Cena","Pausa activa","Otra"))
        spinnerMotivoOnItemSelectedListener()
    }

    fun spinnerMotivoOnItemSelectedListener(){
        spinnerMotivo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                motivo = ""
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                motivo = arrayListOf("Desayuno","Almuerzo","Café tarde","Cena","Pausa activa","Otra")[position]
            }
        }
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