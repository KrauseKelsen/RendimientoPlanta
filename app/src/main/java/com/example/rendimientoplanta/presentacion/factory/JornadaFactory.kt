package com.example.rendimientoplanta.presentacion.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rendimientoplanta.domain.idomain.IJornadaCase

class JornadaFactory (private val iJornadaCase: IJornadaCase): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(IJornadaCase::class.java).newInstance(iJornadaCase)
    }
}