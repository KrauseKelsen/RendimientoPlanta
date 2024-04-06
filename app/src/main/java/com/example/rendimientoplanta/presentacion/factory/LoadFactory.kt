package com.example.rendimientoplanta.presentacion.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rendimientoplanta.domain.idomain.ILoadCase


class LoadFactory (private val iLoadCase: ILoadCase): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ILoadCase::class.java).newInstance(iLoadCase)
    }
}