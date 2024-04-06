package com.example.rendimientoplanta.presentacion.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rendimientoplanta.domain.idomain.IUsuarioCase


class UsuarioFactory (private val iUsuarioCase: IUsuarioCase): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(IUsuarioCase::class.java).newInstance(iUsuarioCase)
    }
}