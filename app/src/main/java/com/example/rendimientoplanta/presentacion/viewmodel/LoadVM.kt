package com.example.rendimientoplanta.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rendimientoplanta.domain.idomain.ILoadCase
import com.example.rendimientoplanta.vo.Resource
import kotlinx.coroutines.Dispatchers

class LoadVM (iLoadCase: ILoadCase): ViewModel() {
    private var piLoadCase = iLoadCase

    fun updPermisos() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLoadCase.PR_UPD_PERMISOS())
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun updFincas() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLoadCase.PR_UPD_MAN_PLANTAS())
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun updLineas() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLoadCase.PR_UPD_MAN_LINEAS())
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun updUsers() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLoadCase.PR_UPD_SEG_USUARIOS())
            } catch (e: Exception) {

                emit(Resource.Failure(e))
            }
        }

    fun updJornadas() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLoadCase.PR_UPD_JORNADAS())
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun delReceso() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLoadCase.DEL_Receso())
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun updOperarios() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLoadCase.PR_UPD_MAN_OPERARIOS())
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun updOperariosLinea() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLoadCase.PR_UPD_MAN_OPERARIOS_LINEA())
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun updTallosAsignados() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLoadCase.PR_UPD_PRO_TALLOS_ASIGNADOS())
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun updTallosDesasignados() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLoadCase.PR_UPD_PRO_TALLOS_DESASIGNADOS())
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun updCierresLinea() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLoadCase.PR_UPD_PRO_CIERRES_LINEA())
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun updCierresOperario() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLoadCase.PR_UPD_PRO_CIERRES_OPERARIO())
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun updRendimiento() =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLoadCase.PR_UPD_MAN_RENDIMIENTO())
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun updall(sql: Boolean) =
        liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(piLoadCase.PR_UPD_ALL_SQL(sql))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
}