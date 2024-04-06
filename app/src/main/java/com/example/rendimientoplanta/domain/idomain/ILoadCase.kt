package com.example.rendimientoplanta.domain.idomain

import com.example.rendimientoplanta.vo.Resource

interface ILoadCase {
    suspend fun PR_UPD_PERMISOS(): Resource<Boolean>
    suspend fun PR_UPD_MAN_PLANTAS(): Resource<Boolean>
    suspend fun PR_UPD_MAN_LINEAS(): Resource<Boolean>
    suspend fun PR_UPD_SEG_USUARIOS(): Resource<Boolean>
    suspend fun PR_UPD_JORNADAS(): Resource<Boolean>
    suspend fun DEL_Receso(): Resource<Boolean>
    suspend fun PR_UPD_MAN_OPERARIOS(): Resource<Boolean>
    suspend fun PR_UPD_MAN_OPERARIOS_LINEA(): Resource<Boolean>
    suspend fun PR_UPD_PRO_TALLOS_ASIGNADOS(): Resource<Boolean>
    suspend fun PR_UPD_PRO_TALLOS_DESASIGNADOS(): Resource<Boolean>
    suspend fun PR_UPD_PRO_CIERRES_LINEA(): Resource<Boolean>
    suspend fun PR_UPD_PRO_CIERRES_OPERARIO(): Resource<Boolean>
    suspend fun PR_UPD_MAN_RENDIMIENTO(): Resource<Boolean>
    suspend fun PR_UPD_ALL_SQL(sql: Boolean): Resource<String>
}