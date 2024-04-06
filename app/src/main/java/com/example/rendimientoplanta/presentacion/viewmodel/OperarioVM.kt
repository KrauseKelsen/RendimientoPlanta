package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.domain.idomain.IOperarioCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class  OperarioVM (iOperarioCase: IOperarioCase): ViewModel() {
    private var piOperarioCase = iOperarioCase

    fun getOperariosIngresados(user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piOperarioCase.GET_OperariosIngresados(user))
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun getOperarioLinea(codigo: Int) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piOperarioCase.GET_OperarioLinea(codigo))
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun getOperario(codigo: Int) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piOperarioCase.GET_Operario(codigo))
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun getOperarios() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piOperarioCase.GET_Operarios())
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun setOperarioEnLinea(codigo: Int, user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piOperarioCase.SET_OperarioEnLinea(codigo, user))
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun getStems() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piOperarioCase.GET_Stems())
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun updOperarioLinea(operarioLinea: OperarioLinea) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piOperarioCase.UPD_OperarioLinea(operarioLinea))
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun updtallosAsignados(operarioLinea: OperarioLinea) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piOperarioCase.UPD_TallosAsignados(operarioLinea))
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun putOperario(identificacion: String, codigo: Int, nombre: String, apellido: String, estado: Boolean, ocupado: Boolean, user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piOperarioCase.PUT_Operario(identificacion, codigo, nombre, apellido, estado, ocupado, user))
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun getOperariosSQL(user: User) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piOperarioCase.GET_OperariosSQL(user))
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }
}