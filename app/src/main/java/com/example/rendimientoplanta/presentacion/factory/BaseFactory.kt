package com.example.rendimientoplanta.presentacion.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rendimientoplanta.domain.idomain.IBaseCase


class BaseFactory (private val iBaseCase: IBaseCase): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(IBaseCase::class.java).newInstance(iBaseCase)
    }
}