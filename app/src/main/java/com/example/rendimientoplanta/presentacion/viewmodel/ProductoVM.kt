package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.domain.idomain.IProductoCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class ProductoVM (iProductoCase: IProductoCase): ViewModel() {
    private var piProductoCase = iProductoCase

    fun getStems(user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piProductoCase.GET_Stems(user))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

}