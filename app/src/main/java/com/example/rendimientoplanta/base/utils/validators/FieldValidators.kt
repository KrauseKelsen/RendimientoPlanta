package com.example.rendimientoplanta.base.utils.validators

import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.res.ResourcesCompat
import com.example.rendimientoplanta.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.content.res.Resources
object FieldValidators {
    fun isValidate(textInputLayout_text: TextInputLayout, edit_text: TextInputEditText, resources: Resources, text :String): Boolean = validateTexto(textInputLayout_text, edit_text, resources, text)
    fun isValidate(textInputLayout_text: TextInputLayout, edit_text: TextInputEditText, resources: Resources, text :String, pass:String): Boolean = validateTexto(textInputLayout_text, edit_text, resources, text, pass)

    fun setupListeners(textInputLayout_text: TextInputLayout, edit_text: TextInputEditText, resources: Resources, text: String) {
        edit_text.addTextChangedListener(TextFieldValidation(textInputLayout_text, edit_text, resources, text))
    }

    class TextFieldValidation(textInputLayout_text: TextInputLayout, edit_text: TextInputEditText, resources: Resources, text: String) : TextWatcher {
        val tilc = textInputLayout_text
        val etc = edit_text
        val reso = resources
        val txt = text
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validateTexto(tilc, etc, reso, txt)
        }

    }

    fun validateTexto(textInputLayout_text: TextInputLayout, edit_text: TextInputEditText, resources: Resources, text: String): Boolean {
        textInputLayout_text.setErrorTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.alert_color, null)))
        if (edit_text.text.toString().trim().isEmpty()) {
            textInputLayout_text.error = text
            edit_text.requestFocus()
            return false
        } else {
            textInputLayout_text.isErrorEnabled = false
        }
        return true
    }

    fun validateTexto(textInputLayout_text: TextInputLayout, edit_text: TextInputEditText, resources: Resources, text: String, pass: String): Boolean {
        textInputLayout_text.setErrorTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.alert_color, null)))
        if (edit_text.text.toString() != pass) {
            textInputLayout_text.error = text
            edit_text.requestFocus()
            return false
        } else {
            textInputLayout_text.isErrorEnabled = false
        }
        return true
    }
}