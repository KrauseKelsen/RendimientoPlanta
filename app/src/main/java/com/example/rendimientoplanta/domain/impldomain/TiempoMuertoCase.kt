package com.example.rendimientoplanta.domain.impldomain

import com.example.rendimientoplanta.base.pojos.Operario
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.data.irepository.ITiempoMuertoRepo
import com.example.rendimientoplanta.domain.idomain.ITiempoMuertoCase
import com.example.rendimientoplanta.vo.Resource

class TiempoMuertoCase(val repo: ITiempoMuertoRepo): ITiempoMuertoCase {
    override suspend fun GET_Operarios(user: User): Resource<ArrayList<Operario>> = repo.GET_Operarios(user)
    override suspend fun SET_TiempoMuerto(operario: Operario, user: User, horaInicio: String, horaFin: String, motivo: String):
            Resource<String> = repo.SET_TiempoMuerto(operario, user, horaInicio, horaFin, motivo)
}