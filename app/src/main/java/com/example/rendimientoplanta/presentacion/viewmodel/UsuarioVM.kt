package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.domain.idomain.IUsuarioCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class UsuarioVM (iUsuarioCase: IUsuarioCase): ViewModel() {
    private var piUsuarioCase = iUsuarioCase

    fun getFincas() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piUsuarioCase.GET_Fincas())
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun getLineas() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piUsuarioCase.GET_Lineas())
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun getPermisos() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piUsuarioCase.GET_Permisos())
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun getUsuarios() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piUsuarioCase.GET_Usuarios())
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun putUsuarios(
        uid: String,
        nombre: String,
        apellido: String,
        email: String,
        miRol: String,
        miFinca: String,
        miLinea: String,
        estado: Boolean
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piUsuarioCase.PUT_Usuarios(uid, nombre, apellido, email, miRol, miFinca, miLinea, estado))
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }
}