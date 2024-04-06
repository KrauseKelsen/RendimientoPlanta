package com.example.rendimientoplanta.presentacion.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rendimientoplanta.domain.idomain.ICierreLineaCase

class CierreLineaFactory (private val iCierreLineaCase: ICierreLineaCase): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ICierreLineaCase::class.java).newInstance(iCierreLineaCase)
    }
}