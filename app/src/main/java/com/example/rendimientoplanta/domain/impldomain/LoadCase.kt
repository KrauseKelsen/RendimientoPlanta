package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.data.irepository.ILoadRepo
import com.example.rendimientoplanta.domain.idomain.ILoadCase
import com.example.rendimientoplanta.vo.Resource

class LoadCase(val repo: ILoadRepo): ILoadCase {
    override suspend fun PR_UPD_PERMISOS(): Resource<Boolean> = repo.PR_UPD_PERMISOS()
    override suspend fun PR_UPD_MAN_PLANTAS(): Resource<Boolean> = repo.PR_UPD_MAN_PLANTAS()
    override suspend fun PR_UPD_MAN_LINEAS(): Resource<Boolean> = repo.PR_UPD_MAN_LINEAS()
    override suspend fun PR_UPD_SEG_USUARIOS(): Resource<Boolean> = repo.PR_UPD_SEG_USUARIOS()
    override suspend fun PR_UPD_JORNADAS(): Resource<Boolean> = repo.PR_UPD_JORNADAS()
    override suspend fun DEL_Receso(): Resource<Boolean> = repo.DEL_Receso()
    override suspend fun PR_UPD_MAN_OPERARIOS(): Resource<Boolean> = repo.PR_UPD_MAN_OPERARIOS()
    override suspend fun PR_UPD_MAN_OPERARIOS_LINEA(): Resource<Boolean> = repo.PR_UPD_MAN_OPERARIOS_LINEA()
    override suspend fun PR_UPD_PRO_TALLOS_ASIGNADOS(): Resource<Boolean> = repo.PR_UPD_PRO_TALLOS_ASIGNADOS()
    override suspend fun PR_UPD_PRO_TALLOS_DESASIGNADOS(): Resource<Boolean> = repo.PR_UPD_PRO_TALLOS_DESASIGNADOS()
    override suspend fun PR_UPD_PRO_CIERRES_LINEA(): Resource<Boolean> = repo.PR_UPD_PRO_CIERRES_LINEA()
    override suspend fun PR_UPD_PRO_CIERRES_OPERARIO(): Resource<Boolean> = repo.PR_UPD_PRO_CIERRES_OPERARIO()
    override suspend fun PR_UPD_MAN_RENDIMIENTO(): Resource<Boolean> = repo.PR_UPD_MAN_RENDIMIENTO()
    override suspend fun PR_UPD_ALL_SQL(sql: Boolean): Resource<String> = repo.PR_UPD_ALL_SQL(sql)

}