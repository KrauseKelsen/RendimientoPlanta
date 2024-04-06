package com.example.rendimientoplanta.base.utils.validators

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import androidx.core.content.res.ResourcesCompat
import android.content.res.Resources
import android.util.Log
import com.example.rendimientoplanta.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

object FieldLoginValidators {

    @SuppressLint("StaticFieldLeak")
    lateinit var textInputLayout_email: TextInputLayout
    lateinit var text_email: TextInputEditText
    @SuppressLint("StaticFieldLeak")
    lateinit var textInputLayout_password: TextInputLayout
    lateinit var text_password: TextInputEditText
    lateinit var resources: Resources

    fun isValidate(ptextInputLayout_email: TextInputLayout, ptext_email: TextInputEditText,ptextInputLayout_password: TextInputLayout, ptext_password: TextInputEditText, presources: Resources): Boolean {
        textInputLayout_email = ptextInputLayout_email
        text_email = ptext_email
        textInputLayout_password = ptextInputLayout_password
        text_password = ptext_password
        resources = presources
        return validateEmail() && validatePassword()
    }

    /**
     * 1) field must not be empty
     * 2) text should matches email address format
     */
    fun validateEmail(ptextInputLayout_email: TextInputLayout, ptext_email: TextInputEditText, presources: Resources): Boolean{
        ptextInputLayout_email.setErrorTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(
            presources, R.color.alert_color, null)))
        if (ptext_email.text.toString().trim().isEmpty()) {
            ptextInputLayout_email.error = "Campo requerido *"
            ptext_email.requestFocus()
            return false
        } else if (!FieldCredentialValidators.isValidEmail(ptext_email.text.toString())) {
            ptextInputLayout_email.error = "Correo inválido *"
            ptext_email.requestFocus()
            return false
        } else {
            ptextInputLayout_email.isErrorEnabled = false
        }
        return true
    }

    /**
     * 1) field must not be empty
     * 2) text should matches email address format
     */
    fun validateEmail(): Boolean{
        textInputLayout_email.setErrorTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(
            resources, R.color.alert_color, null)))
        if (text_email.text.toString().trim().isEmpty()) {
            textInputLayout_email.error = "Campo requerido *"
            text_email.requestFocus()
            return false
        } else if (!FieldCredentialValidators.isValidEmail(text_email.text.toString())) {
            textInputLayout_email.error = "Correo inválido *"
            text_email.requestFocus()
            return false
        } else {
            textInputLayout_email.isErrorEnabled = false
        }
        return true
    }

    /**
     * 1) field must not be empty
     * 2) password lenght must not be less than 6
     * 3) password must contain at least one digit
     * 4) password must contain atleast one upper and one lower case letter
     * 5) password must contain atleast one special character.
     */
    fun validatePassword(ptextInputLayout_password: TextInputLayout, ptext_password: TextInputEditText, presources: Resources): Boolean {
        ptextInputLayout_password.setErrorTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(
            presources, R.color.alert_color, null)))
        if (ptext_password.text.toString().trim().isEmpty()) {
            ptextInputLayout_password.error = "Campo requerido *"
            ptext_password.requestFocus()
            return false
        } else if (ptext_password.text.toString().length < 6) {
            ptextInputLayout_password.error = "Debe añadir al menos 6 caracteres *"
            ptext_password.requestFocus()
            return false
        } else if (!FieldCredentialValidators.isStringContainNumber(ptext_password.text.toString())) {
            ptextInputLayout_password.error = "Debe añadir al menos un digito *"
            ptext_password.requestFocus()
            return false
        } else if (!FieldCredentialValidators.isStringLowerAndUpperCase(ptext_password.text.toString())) {
            ptextInputLayout_password.error =
                "Debe añadir letras mayusculas y minusculas *"
            ptext_password.requestFocus()
            return false
        } else if (!FieldCredentialValidators.isStringContainSpecialCharacter(ptext_password.text.toString())) {
            ptextInputLayout_password.error = "Debe añadir un caracter especial *"
            ptext_password.requestFocus()
            return false
        } else {
            ptextInputLayout_password.isErrorEnabled = false
        }
        return true
    }

    fun validatePassword(): Boolean {
        textInputLayout_password.setErrorTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(
            resources, R.color.alert_color, null)))
        if (text_password.text.toString().trim().isEmpty()) {
            textInputLayout_password.error = "Campo requerido *"
            text_password.requestFocus()
            return false
        } else if (text_password.text.toString().length < 6) {
            textInputLayout_password.error = "Debe añadir al menos 6 caracteres *"
            text_password.requestFocus()
            return false
        } else if (!FieldCredentialValidators.isStringContainNumber(text_password.text.toString())) {
            textInputLayout_password.error = "Debe añadir al menos un digito *"
            text_password.requestFocus()
            return false
        } else if (!FieldCredentialValidators.isStringLowerAndUpperCase(text_password.text.toString())) {
            textInputLayout_password.error =
                "Debe añadir letras mayusculas y minusculas *"
            text_password.requestFocus()
            return false
        } else if (!FieldCredentialValidators.isStringContainSpecialCharacter(text_password.text.toString())) {
            textInputLayout_password.error = "Debe añadir un caracter especial *"
            text_password.requestFocus()
            return false
        } else {
            textInputLayout_password.isErrorEnabled = false
        }
        return true
    }

    fun invalidEmail(TAG: String, msg: String) {
        Log.w(TAG,msg)
        textInputLayout_email.setErrorTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.alert_color, null)))
        text_email.requestFocus()
        textInputLayout_email.error = "El correo no coincide con una cuenta registrada *"
    }

    fun invalidPassword(TAG: String, msg: String) {
        Log.w(TAG,msg)
        textInputLayout_password.setErrorTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.alert_color, null)))
        text_password.requestFocus()
        textInputLayout_password.error = "La contraseña no coincide con el correo ingresado *"
    }
}