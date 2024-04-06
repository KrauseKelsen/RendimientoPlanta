package com.example.rendimientoplanta.presentacion.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rendimientoplanta.domain.idomain.IAsignarProductoCase

class AsignarProductoFactory (private val iAsignarProductoCase: IAsignarProductoCase): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(IAsignarProductoCase::class.java).newInstance(iAsignarProductoCase)
    }
}