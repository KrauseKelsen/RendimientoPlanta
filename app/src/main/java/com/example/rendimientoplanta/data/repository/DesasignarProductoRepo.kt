package com.example.rendimientoplanta.data.repository
import com.example.rendimientoplanta.base.builder.BuilderObject
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.builder.OperarioLineaBuilder
import com.example.rendimientoplanta.base.builder.TallosAsignadosBuilder
import com.example.rendimientoplanta.base.pojos.OperarioLinea
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.pojos.TallosDesasignados
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.base.utils.instances.InstanceRetrofit
import com.example.rendimientoplanta.base.utils.instances.Network
import com.example.rendimientoplanta.base.utils.validators.CalcuTimeValidators
import com.example.rendimientoplanta.data.irepository.IDesasignarProductoRepo
import com.example.rendimientoplanta.data.service.TallosAsignadosService
import com.example.rendimientoplanta.data.service.TallosDesasignadosService
import com.example.rendimientoplanta.pattern.interprete.Contexto
import com.example.rendimientoplanta.vo.Resource
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DesasignarProductoRepo : IDesasignarProductoRepo {
    private val TAG = "DesasignarProductoRepo"

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
                "estado", true, "fincaId", user.fincaId, "lineaId", user.linea)) as ArrayList<TallosAsignados>
        val arraysecundario = ArrayList<TallosAsignados>()
        return if(array.size!=0){
            if(array.size==1){
                Resource.Success(array)
            }else{
                if(CalcuTimeValidators.AwhereGreaterThanB(array[0].horaInicio, array[array.size-1].horaInicio)){
                    arraysecundario.add(array[array.size-2])
                    arraysecundario.add(array[array.size-1])
                }
                else{
                    arraysecundario.add(array[1])
                    arraysecundario.add(array[0])
//                    var tallosAsignados = array[1]
//                    tallosAsignados.horaInicio = array[0].horaFin
//                    FirebaseActions.createDocument(tallosAsignados.uid, "TallosDesasignados", ObjectMapper().convertValue(tallosAsignados, object : TypeReference<Map<String, Any>>() {}))
                }

                Resource.Success(arraysecundario)
            }
        }else{
            Resource.Failure(NullPointerException())
        }
    }

    override suspend fun SET_Stems(operarioLinea: OperarioLinea, user: User, cantidad: Int, motivo: String, tallosAsignados: ArrayList<TallosAsignados>): Resource<Boolean> {
        val horaActual = SimpleDateFormat("HH:mm").format(Date())
        if(CalcuTimeValidators.AwhereGreaterThanToEqualB(tallosAsignados[tallosAsignados.size-1].horaInicio, horaActual)){
            val contexto = Contexto()
            contexto.setHoraVeintiCuatro(tallosAsignados[1].horaInicio, contexto)
            return Resource.Failure(Exception("No se pueden desasignar los tallos al operario seleccionado porque aún no ha iniciado " +
                    "su jornada ya que la misma empieza a las ${contexto.hora12} ${contexto.AMPM}"))
        }

        val tallosDesasignados = ArrayList<TallosDesasignados>()
        val saveStems = tallosAsignados[tallosAsignados.size-1].tallos
        val tallosAsignadosNew = UPD_TallosAsignados(tallosAsignados[tallosAsignados.size-1], cantidad)

        tallosDesasignados.add(
            TallosDesasignados(
                "${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())}|${operarioLinea.fincaId}|${operarioLinea.lineaId}|${operarioLinea.operarioId}|${operarioLinea.user}",
                operarioLinea.fincaId,
                operarioLinea.lineaId,
                operarioLinea.operarioId,
                saveStems,
                saveStems-tallosAsignadosNew.tallos,
                motivo,
                "${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())}",
                operarioLinea.user,
                false
            )
        )

        if(Network.connectedTo()){
            tallosDesasignados[0].sql = PR_UPD_TALLOS_DESASIGNADOS(tallosDesasignados)
        }else{
            tallosDesasignados[0].sql = false
        }

        FirebaseActions.createDocument(tallosDesasignados[0].uid, "TallosDesasignados", ObjectMapper().convertValue(tallosDesasignados[0], object : TypeReference<Map<String, Any>>() {}))

        if(tallosAsignados.size>1){
            tallosAsignados[0].horaInicio = tallosAsignadosNew.horaFin
            FirebaseActions.createDocument(tallosAsignados[0].uid, "TallosAsignados", ObjectMapper().convertValue(tallosAsignados[0], object : TypeReference<Map<String, Any>>() {}))

            val operario = FirebaseActions.GET_OperarioID(operarioLinea.operarioId.toString())
            operario.ocupado = false
            FirebaseActions.createDocument(operario.uid.toString(), "Operarios", ObjectMapper().convertValue(operario, object : TypeReference<Map<String, Any>>() {}))

        }

        FirebaseActions.createDocument(tallosDesasignados[0].uid, "TallosDesasignados", ObjectMapper().convertValue(tallosDesasignados[0], object : TypeReference<Map<String, Any>>() {}))


        return Resource.Success(tallosDesasignados[0].sql && tallosAsignadosNew.sql)
    }

    fun PR_UPD_TALLOS_DESASIGNADOS(jsonTallosDesasignados : ArrayList<TallosDesasignados>) =
        InstanceRetrofit.instanceRetrofit!!.create(TallosDesasignadosService::class.java).upd_tallosDesasignados(jsonTallosDesasignados).execute().isSuccessful

    private fun UPD_TallosAsignados(tallosAsignados: TallosAsignados, cantidad: Int): TallosAsignados {
        val arrayList = ArrayList<TallosAsignados>()
        tallosAsignados.tallos = cantidad
        tallosAsignados.estado = false
        tallosAsignados.horaFin = SimpleDateFormat("HH:mm").format(Date())
        arrayList.add(tallosAsignados)
        if(arrayList.size!=0){
            val tallosAsignados = arrayList[0]
            if(Network.connectedTo()){
                tallosAsignados.sql = PR_UPD_TALLOS_ASIGNADOS(arrayList)
            }else{
                tallosAsignados.sql = false
            }
            FirebaseActions.createDocument(tallosAsignados.uid, "TallosAsignados", ObjectMapper().convertValue(tallosAsignados, object : TypeReference<Map<String, Any>>() {}))
        }
        return arrayList[0]
    }

    fun PR_UPD_TALLOS_ASIGNADOS(jsonTallosAsignados: ArrayList<TallosAsignados>) =
        InstanceRetrofit.instanceRetrofit!!.create(TallosAsignadosService::class.java).upd_tallosAsignados(jsonTallosAsignados).execute().isSuccessful

}