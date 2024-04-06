package com.example.rendimientoplanta.presentacion.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rendimientoplanta.domain.idomain.ITiempoMuertoCase

class TiempoMuertoFactory (private val iTiempoMuertoCase: ITiempoMuertoCase): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ITiempoMuertoCase::class.java).newInstance(iTiempoMuertoCase)
    }
}