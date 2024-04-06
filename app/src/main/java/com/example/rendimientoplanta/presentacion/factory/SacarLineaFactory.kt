package com.example.rendimientoplanta.presentacion.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rendimientoplanta.domain.idomain.ISacarLineaCase


class SacarLineaFactory (private val iSacarLineaCase: ISacarLineaCase): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ISacarLineaCase::class.java).newInstance(iSacarLineaCase)
    }
}