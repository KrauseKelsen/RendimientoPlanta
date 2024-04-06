package com.example.rendimientoplanta.data.repository

import com.example.rendimientoplanta.base.builder.*
import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.base.utils.instances.InstanceRetrofit
import com.example.rendimientoplanta.base.utils.instances.Network
import com.example.rendimientoplanta.base.utils.validators.CalcuTimeValidators
import com.example.rendimientoplanta.data.irepository.IOperarioRepo
import com.example.rendimientoplanta.data.service.*
import com.example.rendimientoplanta.pattern.interprete.Contexto
import com.example.rendimientoplanta.vo.Resource
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OperarioRepo : IOperarioRepo {
    private val TAG = "OperarioRepo"

    fun builderArrayList(builderObject: BuilderObject, querySnapshot: QuerySnapshot): ArrayList<Any> {
        val array = ArrayList<Any>()
        for (change in querySnapshot.documentChanges) {
            if (change.type == DocumentChange.Type.ADDED) {
                MessageBuilder.sourceOrServer(querySnapshot, TAG)
                array.add(builderObject.documentSnapshotToObject(change.document))
            }
        }
        return array
    }

    override suspend fun GET_OperariosIngresados(user: User): Resource<ArrayList<OperarioLinea>> =
        Resource.Success(builderArrayList(OperarioLineaBuilder(),
            FirebaseActions.getColletionWhereEqualTo("OperariosEnLinea", "fincaId", user.fincaId,
                "lineaId", user.linea, "fecha", SimpleDateFormat("yyyy-MM-dd").format(Date()),
                "activo", true)) as ArrayList<OperarioLinea>)

    override suspend fun GET_OperarioLinea(codigo: Int): Resource<OperarioLinea> {
        val querySnapshot = FirebaseActions.getColletionWhereEqualTo("OperariosEnLinea", "operarioId",
            codigo,"activo",true, "fecha", SimpleDateFormat("yyyy-MM-dd").format(Date()))
        for (change in querySnapshot.documentChanges) {
            if (change.type == DocumentChange.Type.ADDED) {
                MessageBuilder.sourceOrServer(querySnapshot, TAG)
                return Resource.Success(OperarioLineaBuilder().documentSnapshotToObject(change.document))
            }
        }
        return Resource.Failure(NullPointerException())
    }

    override suspend fun SET_OperarioEnLinea(codigo: Int, user: User): Resource<Boolean> {
        val array = ArrayList<OperarioLinea>()
        val operario = FirebaseActions.GET_OperarioID(codigo.toString())

        val operarioLinea = OperarioLineaBuilder().operarioLineaBuilder(operario, user, SimpleDateFormat("yyyy-MM-dd").format(Date()),
            SimpleDateFormat("HH:mm").format(Date()), "--:--", true, SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date()))
        array.add(operarioLinea)

        if(Network.connectedTo()){
            operarioLinea.sql = PR_UPD_OPERARIOS_LINEA(array)
        }else{
            operarioLinea.sql = false
        }

        FirebaseActions.createDocument(operarioLinea.uid, "OperariosEnLinea", ObjectMapper().convertValue(operarioLinea, object : TypeReference<Map<String, Any>>() {}))
        return Resource.Success(operarioLinea.sql)
    }

    fun PR_UPD_OPERARIOS_LINEA(jsonOperariosLinea : ArrayList<OperarioLinea>) =
        InstanceRetrofit.instanceRetrofit!!.create(OperarioLineaService::class.java).upd_operariosLinea(jsonOperariosLinea).execute().isSuccessful

    override suspend fun GET_Operario(codigo: Int): Resource<Operario> {
        return Resource.Success(FirebaseActions.GET_OperarioID(codigo.toString()))
    }

    override suspend fun GET_Operarios(): Resource<ArrayList<Operario>> {
        return Resource.Success(builderArrayList(OperarioBuilder(),
            FirebaseActions.getColletion("Operarios")) as ArrayList<Operario>)
    }

    override suspend fun GET_Stems(): Resource<ArrayList<TallosAsignados>> {
        return Resource.Success(builderArrayList(TallosAsignadosBuilder(),
            FirebaseActions.getColletionWhereEqualTo("TallosAsignados", "estado", true,
                "fecha", SimpleDateFormat("yyyy-MM-dd").format(Date()))) as ArrayList<TallosAsignados>)
    }

    override suspend fun UPD_OperarioLinea(operarioLinea: OperarioLinea): Resource<Boolean> {
        val arrayList = ArrayList<OperarioLinea>()
        operarioLinea.horaFin = SimpleDateFormat("HH:mm").format(Date())
        operarioLinea.activo = false
        arrayList.add(operarioLinea)
        if(Network.connectedTo()){
            operarioLinea.sql = PR_UPD_OPERARIOS_LINEA(arrayList)
        }else{
            operarioLinea.sql = false
        }
        FirebaseActions.createDocument(operarioLinea.uid, "OperariosEnLinea", ObjectMapper().convertValue(operarioLinea, object : TypeReference<Map<String, Any>>() {}))

        val operario = FirebaseActions.GET_OperarioID(operarioLinea.operarioId.toString())
        operario.ocupado = false
        FirebaseActions.createDocument(operario.uid.toString(), "Operarios", ObjectMapper().convertValue(operario, object : TypeReference<Map<String, Any>>() {}))
        return Resource.Success(arrayList[0].sql)
    }

    override suspend fun UPD_TallosAsignados(operarioLinea: OperarioLinea): Resource<String> {
        var array = builderArrayList(TallosAsignadosBuilder(),
            FirebaseActions.getColletionWhereEqualTo("TallosAsignados", "operarioLineaId",
                operarioLinea.uid, "estado", true)) as ArrayList<TallosAsignados>

        return if(array.size!=0){
            val tallosAbiertos = array[0]
            if(tallosAbiertos.primero && CalcuTimeValidators.AwhereGreaterThanToEqualB(tallosAbiertos.horaInicio, SimpleDateFormat("HH:mm").format(Date()))){
                val contexto = Contexto()
                contexto.setHoraVeintiCuatro(tallosAbiertos.horaInicio, contexto)
                Resource.Success("No se pueden finalizar la asignación de tallos al operario seleccionado porque aún no ha iniciado " +
                        "su jornada ya que la misma empieza a las ${contexto.hora12} ${contexto.AMPM}")
            }else{
                tallosAbiertos.horaFin = SimpleDateFormat("HH:mm").format(Date())
                tallosAbiertos.estado = false
                if(Network.connectedTo()){
                    val result = PR_UPD_TALLOS_ASIGNADOS(array)
                    array.forEach { tallosAsignados -> tallosAsignados.sql = result}
                }else{
                    array.forEach { tallosAsignados -> tallosAsignados.sql = false}
                }

                FirebaseActions.createDocument(tallosAbiertos.uid, "TallosAsignados", ObjectMapper().convertValue(tallosAbiertos, object : TypeReference<Map<String, Any>>() {}))

                if(array.size>1){
                    array[1].horaInicio = tallosAbiertos.horaFin
                    FirebaseActions.createDocument(array[1].uid, "TallosAsignados", ObjectMapper().convertValue(array[1], object : TypeReference<Map<String, Any>>() {}))
                }

                if(array.size==1){
                    val operario = FirebaseActions.GET_OperarioID(operarioLinea.operarioId.toString())
                    operario.ocupado = false
                    FirebaseActions.createDocument(operario.uid.toString(), "Operarios", ObjectMapper().convertValue(operario, object : TypeReference<Map<String, Any>>() {}))
                }

                if(array.size==1){
                    val contexto = Contexto()
                    contexto.setHoraVeintiCuatro(tallosAbiertos.horaFin, contexto)
                    Resource.Success("Asignación finalizada a las ${contexto.hora12} ${contexto.AMPM}")
                }else{
                    val contexto = Contexto()
                    contexto.setHoraVeintiCuatro(array[1].horaInicio, contexto)
                    Resource.Success("Asignación finalizada. Sin embargo, el operario aún posee una asignación de ${array[1].tallos} tallos " +
                            " a las ${contexto.hora12} ${contexto.AMPM}")
                }
            }
        }
        else
            Resource.Success("El operario ${operarioLinea.nombre} ${operarioLinea.apellidos} no posee tallos asignados.")
    }

    fun PR_UPD_TALLOS_ASIGNADOS(jsonTallosAsignados: ArrayList<TallosAsignados>) =
        InstanceRetrofit.instanceRetrofit!!.create(TallosAsignadosService::class.java).upd_tallosAsignados(jsonTallosAsignados).execute().isSuccessful

    override suspend fun PUT_Operario(identificacion: String, codigo: Int, nombre: String, apellido: String, estado: Boolean, ocupado: Boolean, user: User): Resource<String> {
        var array = ArrayList<Operario>()

        array.add(Operario(codigo, identificacion, nombre, apellido, codigo, estado, ocupado, user.uid,
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date()), false))
        if(Network.connectedTo()){
            val result = PR_UPD_OPERARIOS(array)
            array.forEach { obj -> obj.sql = result}
        }else{
            array.forEach { obj -> obj.sql = false}
        }

        FirebaseActions.createDocument(array[0].uid.toString(), "Operarios",
            ObjectMapper().convertValue(array[0], object : TypeReference<Map<String, Any>>() {}))

        return Resource.Success(if(array[0].sql) "Operario registrado exitosamente en el servidor." else "Se guardaron los datos exitosamente.")
    }

    fun PR_UPD_OPERARIOS(jsonOperarios: ArrayList<Operario>) =
        InstanceRetrofit.instanceRetrofit!!.create(OperarioService::class.java).upd_operario(jsonOperarios).execute().isSuccessful

    override suspend fun GET_OperariosSQL(user: User): Resource<ArrayList<Operario>> {
        val response = InstanceRetrofit.instanceRetrofit!!.create(OperarioService::class.java).upd_operario().execute()
        val listaOperariosSQL = response.body() as ArrayList<Operario>
        for (operario in listaOperariosSQL) {
            PUT_Operario(operario, user)
        }
        return Resource.Success(response.body() as ArrayList<Operario>)
    }

    fun PUT_Operario(operario: Operario, user: User): Boolean {

        var array = ArrayList<Operario>()
        operario.sql = false
        operario.usuarioCreacion = user.uid
        operario.codigo = getCodigo(operario.codigo)
        operario.uid = operario.codigo
        array.add(operario)
        if(Network.connectedTo()){
            val result = PR_UPD_OPERARIOS(array)
            array.forEach { obj -> obj.sql = result}
        }else{
            array.forEach { obj -> obj.sql = false}
        }

        FirebaseActions.createDocument(array[0].uid.toString(), "Operarios",
            ObjectMapper().convertValue(array[0], object : TypeReference<Map<String, Any>>() {}))
        return array[0].sql
    }

    fun getCodigo(codigo: Int) = (codigo+1000)
}