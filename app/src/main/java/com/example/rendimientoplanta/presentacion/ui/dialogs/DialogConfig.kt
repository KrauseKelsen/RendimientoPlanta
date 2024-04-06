package com.example.rendimientoplanta.presentacion.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.pojos.Rendimiento
import com.example.rendimientoplanta.base.pojos.User
import kotlinx.android.synthetic.main.dialog_config.*
import kotlinx.android.synthetic.main.dialog_message.*
import kotlinx.android.synthetic.main.dialog_message.btnCancelar
import kotlinx.android.synthetic.main.dialog_message.btnGuardar

/**
 * El SuccessDialog es el dialogo encargado de mostrar un mensaje de success que es recibido por pararametro en el constructor
 * @autor Krause Kelsen S
 */
class DialogConfig(
    context: Context?,
    private val finalizoDialog: FinalizoDialog,
    fincas: ArrayList<String>,
    lineas: ArrayList<String>,
    user: User,
    rendimiento: Rendimiento
) {
    var finca: String
    var linea: Int
    var rendimientoInt: Int
        /**
         * Esta es la interfaz que envuelve la actividad que llama al dialogo y así mismo con la que guarda un contrato
         * para recibir respuestas o informacion del dialogo
         */
        interface FinalizoDialog {
            /**
             * Este método debe ser sobreescrito por la actividad o fragment que invoque este dialogo.
             * Se llama al momento de presionar "Aceptar" o "Cancelar"
             * Muestra un dialogo de Success
             * @param dialog
             */
            fun resultadoConfigNoDialog(dialog: Dialog?)
            fun resultadoConfigYesDialog(
                dialog: Dialog?,
                obj: String,
                linea: Int,
                rendimientoInt: Int
            )
        }

        /**
         * Recibe el context y la actividad que le invoca y con la que guarda un contrato, para que esta pueda
         * invocar el dialogo, además recibé el mensaje que debe mostrar en el dialogo
         * @param context
         * @param fragment
         * @param mensaje
         */
        init {
            val dialog = Dialog(context!!) //instanciamos el dialogo en la vista
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // le decimos que no queremos titulo
            dialog.setCancelable(false) //impedir que se cancele
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // que se muestre transparente el fondo
            dialog.setContentView(R.layout.dialog_config) //añado el dialogo que voy a mostrar

            dialog.textPlanta.text = user.finca
            dialog.textLinea.text = "Línea ${user.linea}"
            dialog.lb_rendimiento.text = "${rendimiento.rendimientoPorHora} tallos por hora"
            finca = user.finca
            linea = user.linea
            rendimientoInt = rendimiento.rendimientoPorHora

            fincas.sortWith { o1, o2 -> o1.compareTo(o2)}
            lineas.sortWith { o1, o2 -> Integer.parseInt(o1.substring(6)).compareTo(Integer.parseInt(o2.substring(6)))}

            dialog.spinnerPlanta.adapter = ArrayAdapter(context, R.layout.custom_spinner_list_item, fincas)
            spinnerPlantaOnItemSelectedListener(dialog, fincas, user)
            dialog.spinnerLinea.adapter = ArrayAdapter(context, R.layout.custom_spinner_list_item, lineas)
            spinnerLineaOnItemSelectedListener(dialog, lineas, user)
            tv_cantidadTallos_textChange(dialog)
            dialog.btnCancelar.setOnClickListener {finalizoDialog.resultadoConfigNoDialog(dialog)}
            dialog.btnGuardar.setOnClickListener {finalizoDialog.resultadoConfigYesDialog(dialog, finca, linea, rendimientoInt)}
            //se debe hacer el .show para que el dialogo se muestre
            dialog.show()
        }

        fun tv_cantidadTallos_textChange(dialog: Dialog){
            dialog.edit_text_cantidad.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    dialog.lb_rendimiento.text = "${s} tallos por hora"
                }
                override fun afterTextChanged(s: Editable) {}
            })
        }

        fun spinnerPlantaOnItemSelectedListener(dialog: Dialog, fincas: ArrayList<String>, user: User){
            dialog.spinnerPlanta.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    dialog.textPlanta.text = user.finca
                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    finca = fincas[position]
                    dialog.textPlanta.text = finca
                }
            }
        }

        fun spinnerLineaOnItemSelectedListener(dialog: Dialog, lineas: ArrayList<String>, user: User){
            dialog.spinnerLinea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                dialog.textLinea.text = "Línea ${user.linea}"
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                linea = lineas[position].substring(6).toInt()
                dialog.textLinea.text = "Línea ${linea}"
            }
        }


    }
}