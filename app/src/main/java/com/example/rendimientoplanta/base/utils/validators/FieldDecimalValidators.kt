package com.example.rendimientoplanta.base.utils.validators

import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.content.res.Resources
import com.example.rendimientoplanta.R

object FieldDecimalValidators {
    fun isValidate(textInputLayout_cantidad: TextInputLayout, edit_text_cantidad: TextInputEditText, resources: Resources): Boolean =
        validateCantidad(textInputLayout_cantidad, edit_text_cantidad, resources)
    fun isValidate(textInputLayout_cantidad: TextInputLayout, edit_text_cantidad: TextInputEditText, resources: Resources, otraCantidad:Double): Boolean =
        validateContraOtraCantidad(textInputLayout_cantidad, edit_text_cantidad, resources, otraCantidad)
    fun isValidate(textInputLayout_cantidad: TextInputLayout, edit_text_cantidad: TextInputEditText, resources: Resources, rangoIn: Int, rangoFi: Int): Boolean =
        validateRango(textInputLayout_cantidad, edit_text_cantidad, resources, rangoIn, rangoFi)

    fun setupListeners(textInputLayout_cantidad: TextInputLayout, edit_text_cantidad: TextInputEditText, resources: Resources) {
        edit_text_cantidad.addTextChangedListener(TextFieldValidation(textInputLayout_cantidad, edit_text_cantidad, resources))
    }

    class TextFieldValidation(textInputLayout_cantidad: TextInputLayout, edit_text_cantidad: TextInputEditText, resources: Resources) : TextWatcher {
        val tilc = textInputLayout_cantidad
        val etc = edit_text_cantidad
        val reso = resources
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validateCantidad(tilc, etc, reso)
        }

    }

    fun validateCantidad(textInputLayout_cantidad: TextInputLayout, edit_text_cantidad: TextInputEditText, resources: Resources): Boolean {
        textInputLayout_cantidad.setErrorTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.alert_color, null)))
        if (edit_text_cantidad.text.toString().trim().isEmpty()) {
            textInputLayout_cantidad.error = "Campo requerido *"
            edit_text_cantidad.requestFocus()
            return false
        } else if (edit_text_cantidad.text.toString().substring(0, 1) == ".") {
            textInputLayout_cantidad.error = "Debe iniciar con un digito mayor a 0 *"
            edit_text_cantidad.requestFocus()
            return false
        } else if (edit_text_cantidad.text.toString().toDouble() == 0.0) {
            textInputLayout_cantidad.error = "Debe ser un monto diferente a 0 *"
            edit_text_cantidad.requestFocus()
            return false
        } else if (!FieldCredentialValidators.isStringContainNumber(edit_text_cantidad.text.toString())) {
            textInputLayout_cantidad.error = "Debe añadir al menos un digito *"
            edit_text_cantidad.requestFocus()
            return false
        } else {
            textInputLayout_cantidad.isErrorEnabled = false
        }
        return true
    }

    fun validateContraOtraCantidad(textInputLayout_cantidad: TextInputLayout, edit_text_cantidad: TextInputEditText, resources: Resources, otraCantidad: Double): Boolean {
        textInputLayout_cantidad.setErrorTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.alert_color, null)))
        if (edit_text_cantidad.text.toString().trim().isEmpty()) {
            textInputLayout_cantidad.error = "Campo requerido *"
            edit_text_cantidad.requestFocus()
            return false
        } else if (edit_text_cantidad.text.toString().toDouble() > otraCantidad) {
            textInputLayout_cantidad.error = "La cantidad debe ser menor o igual a ${otraCantidad.toInt()} *"
            edit_text_cantidad.requestFocus()
            return false
        } else {
            textInputLayout_cantidad.isErrorEnabled = false
        }
        return true
    }

    fun validateRango(textInputLayout_cantidad: TextInputLayout, edit_text_cantidad: TextInputEditText, resources: Resources, rangoIn: Int, rangoFi: Int, ): Boolean {
        textInputLayout_cantidad.setErrorTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.alert_color, null)))
        if (edit_text_cantidad.text.toString().trim().isEmpty()) {
            textInputLayout_cantidad.error = "Campo requerido *"
            edit_text_cantidad.requestFocus()
            return false
        } else if (edit_text_cantidad.text.toString().toInt() < rangoIn || edit_text_cantidad.text.toString().toInt() > rangoFi) {
            textInputLayout_cantidad.error = "El código estar en un rango de $rangoIn a ${rangoFi}*"
            edit_text_cantidad.requestFocus()
            return false
        } else {
            textInputLayout_cantidad.isErrorEnabled = false
        }
        return true
    }
}