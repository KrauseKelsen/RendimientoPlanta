package com.example.rendimientoplanta.data.repository
import com.example.rendimientoplanta.base.builder.*
import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.base.utils.instances.InstanceRetrofit
import com.example.rendimientoplanta.base.utils.instances.Network
import com.example.rendimientoplanta.base.utils.validators.CalcuTimeValidators
import com.example.rendimientoplanta.data.irepository.ICierreOperarioRepo
import com.example.rendimientoplanta.data.service.CierreOperarioService
import com.example.rendimientoplanta.vo.Resource
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CierreOperarioRepo : ICierreOperarioRepo {
    private val TAG = "CierreOperarioRepo"

    fun builderArrayList(
        builderObject: BuilderObject,
        querySnapshot: QuerySnapshot
    ): ArrayList<Any> {
        val array = ArrayList<Any>()
        for (change in querySnapshot.documentChanges) {
            if (change.type == DocumentChange.Type.ADDED) {
                MessageBuilder.sourceOrServer(querySnapshot, TAG)
                array.add(builderObject.documentSnapshotToObject(change.document))
            }
        }
        return array
    }

    override suspend fun GET_TallosAsignados(cierreLinea: CierreLinea, estado: Boolean): Resource<ArrayList<TallosAsignados>> =
        Resource.Success(getTallosAsignados(cierreLinea, estado))

    suspend fun getTallosAsignados(cierreLinea: CierreLinea, estado: Boolean) : ArrayList<TallosAsignados> {
        val array = builderArrayList(
            TallosAsignadosBuilder(),
            FirebaseActions.getColletionWhereEqualTo(
                "TallosAsignados", "fincaId", cierreLinea.fincaId,
                "lineaId", cierreLinea.lineaId, "fecha", cierreLinea.fechaCierre, "estado", estado
            )
        ) as ArrayList<TallosAsignados>

        val muleta = ArrayList<TallosAsignados>()

        for (tallo in array) {
            if (CalcuTimeValidators.AwhereGreaterThanB(cierreLinea.horaInicio, tallo.horaInicio)) {
                muleta.add(tallo)
            }
        }
        return muleta
    }

    override suspend fun GET_Recesos(user: User): Resource<ArrayList<Receso>> {
        return Resource.Success(
            builderArrayList(
                RecesoBuilder(),
                FirebaseActions.getColletionWhereEqualTo(
                    "Recesos", "fincaId", user.fincaId,
                    "lineaId", user.linea, "fecha", SimpleDateFormat("yyyy-MM-dd").format(Date())
                )
            ) as ArrayList<Receso>
        )
    }

    override suspend fun GET_CierreOperario(
        cierreLinea: CierreLinea,
        operarioId: Int
    ): Resource<CierreOperario> {
        return Resource.Success(FirebaseActions.GET_CierreOperarioID("${cierreLinea.uid}|$operarioId"))
    }

    override suspend fun PUT_CierreOperario(
        user: User,
        tallosParciales: Int,
        tallosAsignados: ArrayList<TallosAsignados>,
        operarioId: Int,
        cierreLinea: CierreLinea,
        recesos: ArrayList<Receso>,
        rendimiento: Rendimiento
    ): Resource<String> {
        val cierreOperario = CierreOperarioBuilder().buildCierreOperario(
            user,
            tallosParciales,
            tallosAsignados,
            operarioId,
            cierreLinea,
            recesos,
            rendimiento
        )

        val array = ArrayList<CierreOperario>()
        array.add(cierreOperario)

        if(Network.connectedTo()){
            array[0].sql = PR_UPD_CIERRE_OPERARIO(array)
        }else{
            array[0].sql = false
        }

        FirebaseActions.createDocument(array[0].uid, "CierreOperario", ObjectMapper()
            .convertValue(array[0], object : TypeReference<Map<String, Any>>() {})).isSuccessful
        return Resource.Success(if(array[0].sql) "Cierre de operario registrado en el servidor." else "Se guardaron los datos exitosamente.")
    }

    fun PR_UPD_CIERRE_OPERARIO(jsonCierreOperarios: ArrayList<CierreOperario>): Boolean = InstanceRetrofit.instanceRetrofit!!.create(
        CierreOperarioService::class.java).upd_cierre_operario(jsonCierreOperarios).execute().isSuccessful



    override suspend fun GET_CierresOperarios(cierreLinea: CierreLineaLoad): Resource<ArrayList<CierreOperarioLoad>> {
        val cierresOperarioLoad = ArrayList<CierreOperarioLoad>()
        val cierresOperarios = builderArrayList(
            CierreOperarioBuilder(), FirebaseActions.getColletionWhereEqualTo("CierreOperario", "cierreLineaId", cierreLinea.uid))
                as ArrayList<CierreOperario>

        val tallosAsignadosCerradosBeforeHour = CalcuTimeValidators.getTallosCerradosBeforeHour(
            builderArrayList(TallosAsignadosBuilder(), FirebaseActions
                .getColletionWhereEqualTo("TallosAsignados",
                    "fincaId", cierreLinea.fincaId,"lineaId", cierreLinea.lineaId,
                    "fecha", cierreLinea.fechaCierre, "estado", false))
                    as ArrayList<TallosAsignados>
            , cierreLinea.horaInicio)
        val tallosAsignadosAbiertosBeforeHour = CalcuTimeValidators.getTallosAbiertosBeforeHour(
            builderArrayList(TallosAsignadosBuilder(), FirebaseActions
                .getColletionWhereEqualTo("TallosAsignados",
                    "fincaId", cierreLinea.fincaId,"lineaId", cierreLinea.lineaId,
                    "fecha", cierreLinea.fechaCierre, "estado", true))
                    as ArrayList<TallosAsignados>
            , cierreLinea.horaInicio)
        var nombreOperario = ""
        var tallosAsignadosInt = 0
        for (cp in cierresOperarios) {
            tallosAsignadosCerradosBeforeHour.forEach {
                if (cp.operarioId == it.operarioId) nombreOperario = "${it.nombreOperario} ${it.apellidosOperario}"
                if (cp.operarioId == it.operarioId) tallosAsignadosInt += it.tallos
            }
            tallosAsignadosAbiertosBeforeHour.forEach {
                if (cp.operarioId == it.operarioId) nombreOperario = "${it.nombreOperario} ${it.apellidosOperario}"
                if (cp.operarioId == it.operarioId) tallosAsignadosInt += it.tallos

            }
            cierresOperarioLoad.add(CierreOperarioLoadBuilder().buildCierreOperarioLoad(cp, nombreOperario, tallosAsignadosInt))
            tallosAsignadosInt = 0
        }
        cierresOperarioLoad.sortWith { o1, o2 -> o2.rendimientoXhora.compareTo(o1.rendimientoXhora)}

        return Resource.Success(cierresOperarioLoad)
    }
}