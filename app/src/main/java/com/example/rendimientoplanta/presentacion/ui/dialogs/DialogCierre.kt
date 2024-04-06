package com.example.rendimientoplanta.presentacion.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.example.rendimientoplanta.R
import kotlinx.android.synthetic.main.dialog_cierre.*

/**
 * El SuccessDialog es el dialogo encargado de mostrar un mensaje de success que es recibido por pararametro en el constructor
 * @autor Krause Kelsen S
 */
class DialogCierre(
    context: Context?,
    private val finalizoDialog: FinalizoDialog,
    label: String?
) {
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
        fun resultadoYesCierreDialog(dialog: Dialog?)
        fun resultadoNoCierreDialog(dialog: Dialog?)
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
        dialog.setContentView(R.layout.dialog_cierre) //añado el dialogo que voy a mostrar

        dialog.labelSuccess.text = label
        dialog.btnCancelar.setOnClickListener {finalizoDialog.resultadoNoCierreDialog(dialog)}
        dialog.btnGuardar.setOnClickListener {finalizoDialog.resultadoYesCierreDialog(dialog)}
        //se debe hacer el .show para que el dialogo se muestre
        dialog.show()
    }
}