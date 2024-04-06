package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.pojos.Motivo
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.data.irepository.IRecesoRepo
import com.example.rendimientoplanta.domain.idomain.IRecesoCase
import com.example.rendimientoplanta.vo.Resource

class RecesoCase(val repo: IRecesoRepo): IRecesoCase{
    override suspend fun PUT_Receso(user: User, motivo: String, horaInicio: String, horaFin: String): Resource<Boolean> = repo.PUT_Receso(user, motivo, horaInicio, horaFin)
    override suspend fun GET_Receso(dispositivo: String): Resource<ArrayList<Motivo>> = repo.GET_Receso(dispositivo)
    override suspend fun DEL_Receso(motivo: Motivo): Resource<Boolean> = repo.DEL_Receso(motivo)
}