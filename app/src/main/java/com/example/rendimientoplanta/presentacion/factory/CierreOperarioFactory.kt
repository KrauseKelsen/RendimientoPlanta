package com.example.rendimientoplanta.presentacion.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rendimientoplanta.domain.idomain.ICierreOperarioCase

class CierreOperarioFactory (private val iCierreOperarioCase: ICierreOperarioCase): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ICierreOperarioCase::class.java).newInstance(iCierreOperarioCase)
    }
}