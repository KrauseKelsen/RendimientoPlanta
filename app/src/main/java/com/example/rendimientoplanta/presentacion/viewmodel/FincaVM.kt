package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.domain.idomain.IFincaCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class FincaVM (iFincaCase: IFincaCase): ViewModel() {
    private var piFincaCase = iFincaCase

    fun getFincas() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piFincaCase.GET_Fincas())
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun putFincas(uid: Int, nombre: String, abreviatura: String, estado: Boolean, user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piFincaCase.PUT_Finca(uid, nombre, abreviatura, estado, user))
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }
}