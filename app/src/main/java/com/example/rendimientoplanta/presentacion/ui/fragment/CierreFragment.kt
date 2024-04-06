package com.example.rendimientoplanta.presentacion.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rendimientoplanta.base.model.CardMenu
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.pojos.ArrayListCierreLinea
import com.example.rendimientoplanta.base.pojos.CierreLinea
import com.example.rendimientoplanta.data.repository.CierreRepo
import com.example.rendimientoplanta.domain.impldomain.CierreCase
import com.example.rendimientoplanta.pattern.interprete.Contexto
import com.example.rendimientoplanta.pattern.interprete.Reloj
import com.example.rendimientoplanta.presentacion.adapter.CardMenuRowAdapter
import com.example.rendimientoplanta.presentacion.factory.CierreFactory
import com.example.rendimientoplanta.presentacion.ui.dialogs.DialogCierre
import com.example.rendimientoplanta.presentacion.viewmodel.CierreVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_cierre.*
import kotlinx.android.synthetic.main.fragment_cierre.recyclerView
import java.text.SimpleDateFormat
import java.util.*

class CierreFragment : BaseFragment(true, true, "Cierre"), CardMenuRowAdapter.onCardMenuClickListener, DialogCierre.FinalizoDialog{
    private val TAG = "CierreFragment"
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            CierreFactory(CierreCase(CierreRepo()))
        ).get(CierreVM::class.java)
    }
    
    var dialogoSeleccionado = true
    var cierreLineaEnCurso = CierreLinea("", 0, 0, "", "", "", 0, 0, 0, 0, 0.0, "", "", "", false,false)
    var contexto = Contexto()
    override fun getViewID():Int = R.layout.fragment_cierre

    fun showDialog(message: Int){
        if(message==0 || message==1)
            DialogCierre(requireContext(), this, getTextDialogCierre(message))
        else
            findNavController().navigate(CierreFragmentDirections.actionCierreFragmentToMessageBottomSheet(getTextDialogCierre(message)))
    }

    fun getTextDialogCierre(message : Int):String{
        return when (message) {
            0 -> "¿Desea comenzar el cierre de operarios a las ${updateContexto(true).hora12} ${contexto.AMPM}?, una " +
                    "vez comenzado todos los operarios quedarán registrados a esta hora y esta acción no se podrá deshacer"
            1 -> "¿Desea finalizar el cierre de operarios a las ${updateContexto(false).hora12} ${contexto.AMPM}?, una " +
                    "vez finalizado no podrá cerrar mas operarios y se realizará el cierre de línea, esta acción no se podrá deshacer"
            2 -> "Se ha iniciado un cierre de operarios con anterioridad en la línea ${user.linea} de la finca ${user.finca}, " +
                    "fecha ${cierreLineaEnCurso.fechaCierre} ${cierreLineaEnCurso.horaInicio}. " +
                    "Por favor realice el cierre de operarios respectivo y finalice el cierre de línea para comenzar otro nuevo."
            3 -> "No existe un cierre de línea actual en la línea ${user.linea} de la finca ${user.finca}. Porfavor inicie un cierre de línea para continuar"
            4 -> "La línea ${user.linea} de la finca ${user.finca} no posee cierres de línea el día de hoy." +
                    "\nPor favor inicie un cierre de línea y cierre sus operarios para continuar."
            else -> ""
        }
    }

    fun updateContexto(dialogo : Boolean): Contexto {
        dialogoSeleccionado = dialogo
        contexto.hora24 = SimpleDateFormat("HH:mm").format(Date())
        Reloj().interpretar24(contexto)
        return contexto
    }

    fun setBackgroudColorButton(selected: Boolean) {
        if (selected) {
            setBackgroudColorButton(btnFinalizar, false)
            setBackgroudColorButton(btnEmpezar, true)
        }else{
            setBackgroudColorButton(btnFinalizar, true)
            setBackgroudColorButton(btnEmpezar, false)
        }
    }

    fun setBackgroudColorButton(btn: Button, selected: Boolean) {
        if(selected){
            btn.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_gradiend_buttom)
            btn.setTextColor(Color.parseColor("#FFFFFFFF"))
        }else{
            btn.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_gradiend_buttom_white)
            btn.setTextColor(Color.parseColor("#DB6302"))
        }
    }

    fun setOnClickBackgroud(){
        btnEmpezar.setOnClickListener {
            setBackgroudColorButton(true)
            if(cierreLineaEnCurso.uid != "") showDialog(2) else showDialog(0)

        }
        btnFinalizar.setOnClickListener {
            setBackgroudColorButton(false)
            if(cierreLineaEnCurso.uid != "") showDialog(1) else showDialog(3)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        getCierreLineaAbierto()
        setupRecyclerView()
        setOnClickBackgroud()
    }

    override fun resultadoYesCierreDialog(dialog: Dialog?) {
        dialog?.dismiss()
        Log.e(TAG, "Dialogo Aceptado")
        if (dialogoSeleccionado) setCierreLinea() else setCierreLinea(cierreLineaEnCurso, contexto.hora24)
    }

    private fun setCierreLinea() {
        viewModel.setCierreLinea(user, contexto.hora24).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Empezando cierre de línea...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Cierre de línea iniciado")
                    showProgressBar(false, progress_horizontal)
                    cierreLineaEnCurso = result.data
                    setBackgroudColorButton(true)
                }
                is Resource.Failure -> {
                    findNavController().navigate(CierreFragmentDirections
                        .actionCierreFragmentToMessageBottomSheet
                            ("Error al comenzar el cierre de línea: \nMotivo_: ${result.exception.message}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    private fun setCierreLinea(cierreLinea: CierreLinea, horaFin: String) {
        viewModel.setCierreLinea(user, cierreLinea, horaFin, rendimiento).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Finalizando el cierre de línea...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Cierre de línea finalizado")
                    showProgressBar(false, progress_horizontal)
                    cierreLineaEnCurso = CierreLinea("", 0, 0, "", "", "", 0, 0, 0, 0, 0.0, "", "", "", false,false)
                    setBackgroudColorButton(false)
                }
                is Resource.Failure -> {
                    findNavController().navigate(CierreFragmentDirections
                        .actionCierreFragmentToMessageBottomSheet
                            ("Error al finalizar el cierre de línea: \nMotivo_: ${result.exception.message}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    private fun getCierreLineaAbierto() {
        viewModel.getCierreLineaAbierto(user).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Buscando cierre de línea abierto...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Cierre de línea encontrado")
                    showProgressBar(false, progress_horizontal)
                    if(result.data.size!=0) cierreLineaEnCurso = result.data[0]
                    setBackgroudColorButton(result.data.size!=0)
                }
                is Resource.Failure -> {
                    findNavController().navigate(CierreFragmentDirections
                        .actionCierreFragmentToMessageBottomSheet
                            ("Error al validar el cierre de línea: \nMotivo_: ${result.exception.message}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }

    override fun resultadoNoCierreDialog(dialog: Dialog?) {
        dialog?.dismiss()
        Log.e(TAG, "Dialogo Cancelado")
        if (cierreLineaEnCurso.uid!="") setBackgroudColorButton(true) else setBackgroudColorButton(false)
    }

    private fun setupRecyclerView() {
        val list = listOf(
                CardMenu(R.drawable.card_menu_cierreoperario,1),
                CardMenu(R.drawable.card_menu_cierrelinea,2)
        )
        recyclerView.adapter = CardMenuRowAdapter(requireContext(), list, this)
    }

    override fun onItemClick(id: Int) {
        when(id){
            1 -> if (cierreLineaEnCurso.uid!="") findNavController().navigate(CierreFragmentDirections.actionCierreFragmentToCierreOperarioFragment(cierreLineaEnCurso)) else showDialog(3)
            2 -> getCierreLineaCerrado()
        }
    }

    private fun getCierreLineaCerrado() {
        viewModel.getCierreLineaCerrado(user).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progress_horizontal)
                    Log.w(TAG, "Buscando cierre de línea cerrado...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Cierre de línea cerrado encontrado")
                    showProgressBar(false, progress_horizontal)
                    if(result.data.size!=0)
                        if(user.rol=="Administrador de planta" || user.rol=="Administrador")
                            findNavController().navigate(CierreFragmentDirections.actionCierreFragmentToCierreLineaAdmistradorFragment(
                                ArrayListCierreLinea(result.data)
                            ))
                        else
                            findNavController().navigate(CierreFragmentDirections.actionCierreFragmentToCierreLineaFragment(
                                ArrayListCierreLinea(result.data)))
                    else showDialog(4)
                }
                is Resource.Failure -> {
                    findNavController().navigate(CierreFragmentDirections
                        .actionCierreFragmentToMessageBottomSheet
                            ("Error al validar el cierre de línea: \nMotivo_: ${result.exception.message}"))
                    showProgressBar(false, progress_horizontal)
                }
            }
        })
    }
}