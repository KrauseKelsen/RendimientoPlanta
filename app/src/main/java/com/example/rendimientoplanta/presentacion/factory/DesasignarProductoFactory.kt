package com.example.rendimientoplanta.presentacion.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rendimientoplanta.domain.idomain.IDesasignarProductoCase

class DesasignarProductoFactory (private val iDesasignarProductoCase: IDesasignarProductoCase): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(IDesasignarProductoCase::class.java).newInstance(iDesasignarProductoCase)
    }
}