package com.example.rendimientoplanta.data.repository
import com.example.rendimientoplanta.base.builder.*
import com.example.rendimientoplanta.base.pojos.Jornada
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.base.utils.instances.InstanceRetrofit
import com.example.rendimientoplanta.base.utils.instances.Network
import com.example.rendimientoplanta.base.utils.validators.CalcuTimeValidators
import com.example.rendimientoplanta.data.irepository.IAsignarProductoRepo
import com.example.rendimientoplanta.data.service.TallosAsignadosService
import com.example.rendimientoplanta.pattern.interprete.Contexto
import com.example.rendimientoplanta.vo.Resource
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AsignarProductoRepo : IAsignarProductoRepo {
    private val TAG = "AsignarProductoRepo"

    fun builderArrayList(builderObject: BuilderObject, querySnapshot: QuerySnapshot) : ArrayList<Any>{
        val array = ArrayList<Any>()
        for (change in querySnapshot.documentChanges) {
            if (change.type == DocumentChange.Type.ADDED) {
                MessageBuilder.sourceOrServer(querySnapshot, TAG)
                array.add(builderObject.documentSnapshotToObject(change.document))
            }
        }
        return array
    }

    override suspend fun GET_Jornada(user: User): Resource<ArrayList<Jornada>> {
        val querySnapshot = FirebaseActions.
        getColletionWhereEqualTo("Jornadas", "fecha", SimpleDateFormat("yyyy-MM-dd").format(Date()),
            "fincaId", user.fincaId, "lineaId", user.linea)
        return Resource.Success(builderArrayList(JornadaBuilder(), querySnapshot) as ArrayList<Jornada>)
    }

    override suspend fun GET_OperariosEnLinea(user: User): Resource<ArrayList<OperarioLinea>> {
        val array = builderArrayList(OperarioLineaBuilder(),
            FirebaseActions.getColletionWhereEqualTo("OperariosEnLinea", "activo", true,
                "fincaId", user.fincaId, "lineaId", user.linea, "fecha", SimpleDateFormat("yyyy-MM-dd").format(Date())))
        as ArrayList<OperarioLinea>
        return if (array.size!=0)
            Resource.Success(array)
        else
            Resource.Failure(NullPointerException())
    }

    override suspend fun GET_Stems(codigo: Int, user: User): Resource<ArrayList<TallosAsignados>> {
        val array = builderArrayList(TallosAsignadosBuilder(),
            FirebaseActions.getColletionWhereEqualTo("TallosAsignados", "operarioId", codigo,
                "fecha", SimpleDateFormat("yyyy-MM-dd").format(Date()), "fincaId", user.fincaId, "lineaId", user.linea)) as ArrayList<TallosAsignados>
        return if (array.size!=0)
            Resource.Success(array)
        else
            Resource.Failure(NullPointerException())
    }

    override suspend fun SET_Stems(
        operarioLinea: OperarioLinea,
        user: User,
        cantidad: Int,
        jornada: Jornada,
        tipoTalloSeleccionado: String
    ): Resource<Boolean> {
        var horaInicio: String = SimpleDateFormat("HH:mm").format(Date())
        var primero = false
        var segundo = false
        var array = ArrayList<TallosAsignados>()
        //valido si es el primer tallo que se va registrar hoy (trae tallos cerrados o abiertos)0
        val primeroArray = builderArrayList(TallosAsignadosBuilder(),
            FirebaseActions.getColletionWhereEqualTo("TallosAsignados",
                "operarioId", operarioLinea.operarioId, "fecha", SimpleDateFormat("yyyy-MM-dd").format(Date())))
                as ArrayList<TallosAsignados>

        //es el primero que registran de ese operario hoy
        if(primeroArray.size==0) {
            horaInicio = GET_HoraInicioStem(jornada)
            primero = true
        }
        // es el segundo tallo que registran de ese operario hoy
        else if(primeroArray.size==1){
            //no ha iniciado nisiquiera el primer tallo, porque la jornada no ha empezado
            if(primeroArray[0].primero && CalcuTimeValidators.AwhereGreaterThanToEqualB(primeroArray[0].horaInicio, SimpleDateFormat("HH:mm").format(Date()))){
                val contexto = Contexto()
                contexto.setHoraVeintiCuatro(jornada.horaInicio, contexto)
                return Resource.Failure(Exception("No se pueden asignar tallos al operario seleccionado porque ya posee una asignación a las " +
                        "${contexto.hora12} ${contexto.AMPM}, espere a que llegue la hora para que el operario comience su asignación " +
                        "para realizar una reasignación."))
            }
            // ya empezó la primera asignación, quizá ya está por terminarla
            horaInicio = SimpleDateFormat("HH:mm").format(Date())
            segundo = true
        }
        // han registrado mas de dos tallos hoy
        else{
            if(primeroArray[primeroArray.size-2].estado){
                // ya lleva dos tallos abiertos anteriormente
                val contexto = Contexto()
                contexto.setHoraVeintiCuatro(jornada.horaInicio, contexto)
                return Resource.Failure(Exception("No se pueden asignar tallos al operario seleccionado porque ya posee dos asignaciones, " +
                        "finalice una de las asignaciones para realizar una reasignación."))
            }
            val horaInicioActual = SimpleDateFormat("HH:mm").format(Date())

            horaInicio = horaInicioActual
        }

        array.add(
            TallosAsignadosBuilder()
                .buildTallosAsignados(operarioLinea, user, cantidad, horaInicio, primero, segundo, tipoTalloSeleccionado))

        if(Network.connectedTo()){
            val result = PR_UPD_TALLOS_ASIGNADOS(array)
            array.forEach { tallosAsignados -> tallosAsignados.sql = result}
        }else{
            array.forEach { tallosAsignados -> tallosAsignados.sql = false}
        }

        array.forEach { tallosAsignados ->  FirebaseActions.createDocument(tallosAsignados.uid, "TallosAsignados", ObjectMapper().convertValue(tallosAsignados, object : TypeReference<Map<String, Any>>() {}))}

        val operario = FirebaseActions.GET_OperarioID(operarioLinea.operarioId.toString())
        operario.ocupado = true
        FirebaseActions.createDocument(operario.uid.toString(), "Operarios", ObjectMapper().convertValue(operario, object : TypeReference<Map<String, Any>>() {}))

        return Resource.Success(array[array.size-1].sql)
    }

    override suspend fun SET_Stem(array: ArrayList<TallosAsignados>): Resource<Boolean> {
        if(Network.connectedTo()){
            val result = PR_UPD_TALLOS_ASIGNADOS(array)
            array.forEach { tallosAsignados ->  tallosAsignados.sql = result}
        }else{
            array.forEach { tallosAsignados ->  tallosAsignados.sql = false}
        }

        array.forEach {tallosAsignados -> FirebaseActions.createDocument(tallosAsignados.uid, "TallosAsignados",
            ObjectMapper().convertValue(tallosAsignados, object : TypeReference<Map<String, Any>>() {}))}

        return Resource.Success(array[array.size-1].sql)
    }

    private fun GET_HoraInicioStem(jornada: Jornada) : String {
        var horaInicio = jornada.horaInicio
        if(CalcuTimeValidators.AwhereGreaterThanToEqualB(SimpleDateFormat("HH:mm").format(Date()), jornada.horaInicio))
            horaInicio = SimpleDateFormat("HH:mm").format(Date())
        return horaInicio
    }


    fun PR_UPD_TALLOS_ASIGNADOS(jsonTallosAsignados: ArrayList<TallosAsignados>) =
        InstanceRetrofit.instanceRetrofit!!.create(TallosAsignadosService::class.java).upd_tallosAsignados(jsonTallosAsignados).execute().isSuccessful

}