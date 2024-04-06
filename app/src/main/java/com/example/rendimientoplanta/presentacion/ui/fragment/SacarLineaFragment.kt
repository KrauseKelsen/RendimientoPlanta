package com.example.rendimientoplanta.presentacion.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.utils.validators.FieldDecimalValidators
import com.example.rendimientoplanta.base.utils.validators.FieldValidators
import com.example.rendimientoplanta.data.repository.BaseRepo
import com.example.rendimientoplanta.data.repository.SacarLineaRepo
import com.example.rendimientoplanta.domain.impldomain.BaseCase
import com.example.rendimientoplanta.domain.impldomain.SacarLineaCase
import com.example.rendimientoplanta.pattern.interprete.Contexto
import com.example.rendimientoplanta.pattern.interprete.Reloj
import com.example.rendimientoplanta.presentacion.factory.BaseFactory
import com.example.rendimientoplanta.presentacion.factory.SacarLineaFactory
import com.example.rendimientoplanta.presentacion.viewmodel.BaseVM
import com.example.rendimientoplanta.presentacion.viewmodel.SacarLineaVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_sacar_linea.*
import kotlinx.android.synthetic.main.fragment_sacar_linea.btnGuardar
import kotlinx.android.synthetic.main.fragment_sacar_linea.edit_text_cantidad
import kotlinx.android.synthetic.main.fragment_sacar_linea.edit_text_motivo
import kotlinx.android.synthetic.main.fragment_sacar_linea.progress_horizontal
import kotlinx.android.synthetic.main.fragment_sacar_linea.textInputLayout_cantidad
import kotlinx.android.synthetic.main.fragment_sacar_linea.textInputLayout_motivo
import kotlinx.android.synthetic.main.fragment_sacar_linea.tv_horaInicio

class SacarLineaFragment : BaseFragment(true, true, "Sacar Linea") {
    private val args: SacarLineaFragmentArgs by navArgs()

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            SacarLineaFactory(SacarLineaCase(SacarLineaRepo()))
        ).get(SacarLineaVM::class.java)
    }


    private val viewModelBase by lazy {
        ViewModelProvider(
            this,
            BaseFactory(BaseCase(BaseRepo()))
        ).get(BaseVM::class.java)
    }

    private val TAG = "SacarLineaFragment"
    lateinit var tallosAsignados : TallosAsignados

    override fun getViewID(): Int = R.layout.fragment_sacar_linea

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showProgressBar(false, progress_horizontal)
        getTallosPorHora(args.operarioLinea!!, rendimientoPorHora, progress_horizontal)
        tv_nombreOperario.text = "${args.operarioLinea?.nombre} ${args.operarioLinea?.apellidos}"
        tv_cantidadTallos_textChange()
        tv_motivo_textChange()
        FieldValidators.setupListeners(textInputLayout_motivo, edit_text_motivo, resources, "Campo requerido *")
        tallosAsignados = args.tallosAsignados!!.arrayList[0]
        asignarDatos(args.tallosAsignados!!.arrayList[0])

        btnGuardar.setOnClickListener {
            if(FieldDecimalValidators.isValidate(textInputLayout_cantidad, edit_text_cantidad, resources, tallosAsignados.tallos.toDouble())
                && FieldValidators.isValidate(textInputLayout_motivo, edit_text_motivo, resources, "Campo requerido *")){
                sacarLinea()
            }
        }
    }

    private fun sacarLinea() {
        Log.w(TAG, "Sacar de la línea")
        viewModel.sacarLinea(args.operarioLinea!!, args.tallosAsignados!!.arrayList, edit_text_cantidad.text.toString().toInt(), edit_text_motivo.text.toString()).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Sacando operario de la linea...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Operario sacado de la linea")
                    showProgressBar(false, progress_horizontal)
                    findNavController().navigate(
                        SacarLineaFragmentDirections.actionSacarLineaFragmentToMessageBottomSheet(
                            "Han sido desasignados ${args.tallosAsignados!!.arrayList[0].tallos-edit_text_cantidad.text.toString().toInt()} tallos al operario " +
                                    "${args.operarioLinea!!.nombre} ${args.operarioLinea!!.apellidos}"))
                    btnGuardar.isClickable = false
                    findNavController().popBackStack() // retrocede
                }
                is Resource.Failure -> {
                    showProgressBar(false, progress_horizontal)
                    findNavController().navigate(SacarLineaFragmentDirections
                        .actionSacarLineaFragmentToMessageBottomSheet("Error al sacar de la línea. \nMotivo: ${result.exception.message.toString()}"))
                }
            }
        })
    }

    fun asignarDatos(data: TallosAsignados) {
        val contexto = Contexto()
        contexto.setHoraVeintiCuatro(data.horaInicio, contexto)
        tv_tallosAsignados.text = "${data.tallos} tallos"
        tv_horaInicio.text = "${contexto.hora12}"
        tv_Inicio.text = "${contexto.AMPM}"
    }

    fun tv_cantidadTallos_textChange(){
        edit_text_cantidad.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                tv_cantidadTallos.text = "Cantidad de tallos: ${s}"
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun tv_motivo_textChange(){
        edit_text_motivo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                tv_motivo.text = "Motivo: ${s}"
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

}