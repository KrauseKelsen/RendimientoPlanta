package com.example.rendimientoplanta.presentacion.ui.fragment

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.pojos.Jornada
import com.example.rendimientoplanta.data.repository.JornadaRepo
import com.example.rendimientoplanta.domain.impldomain.JornadaCase
import com.example.rendimientoplanta.pattern.interprete.Contexto
import com.example.rendimientoplanta.presentacion.factory.JornadaFactory
import com.example.rendimientoplanta.presentacion.viewmodel.JornadaVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_jornada.*
import kotlinx.android.synthetic.main.fragment_jornada.cv_fin
import kotlinx.android.synthetic.main.fragment_jornada.cv_inicio
import kotlinx.android.synthetic.main.fragment_jornada.progress_horizontal
import kotlinx.android.synthetic.main.fragment_jornada.tv_Fin
import kotlinx.android.synthetic.main.fragment_jornada.tv_Inicio
import kotlinx.android.synthetic.main.fragment_jornada.tv_horaFin
import kotlinx.android.synthetic.main.fragment_jornada.tv_horaInicio
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class JornadaFragment : BaseFragment(true, true, "Jornada") {
    private val TAG = "JornadaFragment"

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            JornadaFactory(JornadaCase(JornadaRepo()))
        ).get(JornadaVM::class.java)
    }

    override fun getViewID(): Int = R.layout.fragment_jornada

    lateinit var jornada: Jornada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textLineaSol.text = "Línea ${user.linea}"
        textLineaNoche.text = "Línea ${user.linea}"
        getJornada()
        cv_inicio.setOnClickListener { openTimePicker(0, R.style.DatePickerDialogTheme_Primary) }
        cv_fin.setOnClickListener { openTimePicker(1, R.style.DatePickerDialogTheme_Green) }
    }

    private fun openTimePicker(time: Int, datepickerDialogTheme: Int) {
        val hourCurrent: Int
        val minsCurrent: Int
        if(time==0){
            hourCurrent = Integer.parseInt(tv_horaInicio.text.toString().split(":")[0])
            minsCurrent = Integer.parseInt(tv_horaInicio.text.toString().split(":")[1])
        }else{
            hourCurrent = Integer.parseInt(tv_horaFin.text.toString().split(":")[0])
            minsCurrent = Integer.parseInt(tv_horaFin.text.toString().split(":")[1])
        }

        TimePickerDialog(
            requireContext(), datepickerDialogTheme,
            { _, selectedHour, selectedMinute ->
                if (time == 0) setHora(
                    tv_horaInicio,
                    tv_Inicio,
                    "$selectedHour:$selectedMinute"
                ) else setHora(tv_horaFin, tv_Fin, "$selectedHour:$selectedMinute")
            },
            hourCurrent, minsCurrent, false
        ).show() //Yes 24 hour time
    }

    private fun setHora(tv_hora: TextView, tv_tiempo: TextView, time: String) {
        val contexto = Contexto()
        contexto.setHoraVeintiCuatro("${Integer.parseInt(time.split(":")[0])}:${Integer.parseInt(time.split(":")[1])}", contexto)
        tv_hora.text = contexto.hora12
        tv_tiempo.text = contexto.AMPM
        setJornada()
    }

    fun getJornada() {
        viewModel.getJornada(user).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "${MessageBuilder.sLoading} Obteniendo jornada del usuario ${user.uid}")
                }
                is Resource.Success -> {
                    Log.w(TAG, "${MessageBuilder.sSuccess} Respuesta success: ${result.data}")
                    if (result.data.size!=0){
                        val jornada = result.data[0]
                        setHoras(tv_horaInicio, tv_Inicio, jornada.horaInicio)
                        setHoras(tv_horaFin, tv_Fin, jornada.horaFin)
                    }else{
                        setHoras()
                    }
                    showProgressBar(false, progress_horizontal)
                }
                is Resource.Failure -> {
                    //esto no es un error ya que simplemente no hay hora registrada y por lo tanto se setea una nueva
                    setHoras()
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    private fun setHoras() {
        setHora(tv_horaInicio, tv_Inicio, "${SimpleDateFormat("hh").format(Date()).toInt()}:${SimpleDateFormat("mm").format(Date())}")
        setHora(tv_horaFin,tv_Fin, "${SimpleDateFormat("hh").format(Date()).toInt()}:${SimpleDateFormat("mm").format(Date())}")
    }

    private fun setHoras(tv_hora: TextView, tv_tiempo: TextView, hora: String) {
        val contexto = Contexto()
        contexto.setHoraVeintiCuatro(hora, contexto)
        tv_hora.text = contexto.hora12
        tv_tiempo.text = contexto.AMPM
    }

    fun setJornada() {
        viewModel.setJornada(user, "${tv_horaInicio.text} ${tv_Inicio.text}", "${tv_horaFin.text} ${tv_Fin.text}")
            .observe(viewLifecycleOwner, { result ->
                when (result) {
                    is Resource.Loading -> {
                        showProgressBar(true, progress_horizontal)
                        Log.w(TAG, "${MessageBuilder.sLoading} Registrando la jornada del usuario ${user.uid}")
                    }
                    is Resource.Success -> {
                        Log.w(TAG, "${MessageBuilder.sSuccess} Respuesta success: ${result.data}")
                        showProgressBar(false, progress_horizontal)
                    }
                    is Resource.Failure -> {
                        when(result.exception){
                            !is IllegalArgumentException ->{
                                findNavController().navigate(JornadaFragmentDirections.actionJornadaFragmentToMessageBottomSheet(
                                    "Error al guardar la jornada.\nMotivo: ${result.exception.message}"))
                            }
                        }
                        Log.w(TAG,"${MessageBuilder.sFailure} Respuesta excepcion: ${result.exception.message}")
                        showProgressBar(false, progress_horizontal)
                    }
                }
            })
    }
}