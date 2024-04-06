package com.example.rendimientoplanta.data.repository

import com.example.rendimientoplanta.base.builder.*
import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.base.utils.instances.InstanceRetrofit
import com.example.rendimientoplanta.base.utils.instances.Network
import com.example.rendimientoplanta.data.irepository.ILoadRepo
import com.example.rendimientoplanta.data.service.*
import com.example.rendimientoplanta.vo.Resource
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.firestore.DocumentChange

class LoadRepo : ILoadRepo {
    private val TAG = "LoadRepo"

    suspend fun builderArrayList(builderObject: BuilderObject, collection: String): ArrayList<Any> {
        val array = ArrayList<Any>()
        val querySnapshot = FirebaseActions.getCollectionSQL(collection)
        for (change in querySnapshot.documentChanges) {
            if (change.type == DocumentChange.Type.ADDED) {
                MessageBuilder.sourceOrServer(querySnapshot, TAG)
                array.add(builderObject.documentSnapshotToObject(change.document))
            }
        }
        return array
    }

    suspend fun builderArrayListAll(builderObject: BuilderObject, collection: String): ArrayList<Any> {
        val array = ArrayList<Any>()
        val querySnapshot = FirebaseActions.getAllCollectionSQL(collection)
        for (change in querySnapshot.documentChanges) {
            if (change.type == DocumentChange.Type.ADDED) {
                MessageBuilder.sourceOrServer(querySnapshot, TAG)
                array.add(builderObject.documentSnapshotToObject(change.document))
            }
        }
        return array
    }

    override suspend fun PR_UPD_PERMISOS(): Resource<Boolean> {
        return if(Network.connectedTo()) {
            val array = builderArrayList(PermisoBuilder(),"Permisos") as ArrayList<Permiso>
            val nombres = ArrayList<String>()
            array.forEach {permiso -> nombres.add(permiso.uid)}
            if(PR_CHANGE_OPCIONESXGRUPO(nombres)){
                if(PR_UPD_OPCIONESXGRUPO(array)){
                    for(obj in array){
                        obj.sql = true
                        FirebaseActions.createDocument(obj.uid, "Permisos", ObjectMapper().convertValue(obj, object : TypeReference<Map<String, Any>>() {}))
                    }
                    Resource.Success(true)
                } else
                    Resource.Success(false)
            } else
                Resource.Success(false)
        }else
            Resource.Success(false)
    }

    fun PR_CHANGE_OPCIONESXGRUPO(jsonPermisos : ArrayList<String>): Boolean = InstanceRetrofit.instanceRetrofit!!.create(DelPermisoService::class.java).upd_delpermiso(jsonPermisos).execute().isSuccessful

    fun PR_UPD_OPCIONESXGRUPO(jsonPermisos: ArrayList<Permiso>): Boolean = InstanceRetrofit.instanceRetrofit!!.create(PermisoService::class.java).upd_permiso(jsonPermisos).execute().isSuccessful

    override suspend fun PR_UPD_MAN_PLANTAS(): Resource<Boolean> {
        return if(Network.connectedTo()){
            val array = builderArrayList(FincaBuilder(), "Fincas") as ArrayList<Finca>
            if(PR_UPD_FINCA(array)){
                for(obj in array){
                    obj.sql = true
                    FirebaseActions.createDocument(obj.uid.toString(), "Fincas", ObjectMapper().convertValue(obj, object : TypeReference<Map<String, Any>>() {}))
                }
                Resource.Success(true)
            } else
                Resource.Success(false)
        }else
            Resource.Success(false)
    }

    fun PR_UPD_FINCA(jsonFincas: ArrayList<Finca>): Boolean = InstanceRetrofit.instanceRetrofit!!.create(FincaService::class.java).upd_finca(jsonFincas).execute().isSuccessful

    override suspend fun PR_UPD_MAN_LINEAS(): Resource<Boolean> {
        return if(Network.connectedTo()){
            val array = builderArrayList(LineaBuilder(), "Lineas") as ArrayList<Linea>
            if(PR_UPD_LINEA(array)){
                for(obj in array){
                    obj.sql = true
                    FirebaseActions.createDocument(obj.uid.toString(), "Lineas", ObjectMapper().convertValue(obj, object : TypeReference<Map<String, Any>>() {}))
                }
                Resource.Success(true)
            } else
                Resource.Success(false)
        }else
            Resource.Success(false)
    }

    fun PR_UPD_LINEA(jsonLineas: ArrayList<Linea>): Boolean = InstanceRetrofit.instanceRetrofit!!.create(LineaService::class.java).upd_linea(jsonLineas).execute().isSuccessful

    override suspend fun PR_UPD_SEG_USUARIOS(): Resource<Boolean> {
        return if(Network.connectedTo()){
            val array = builderArrayList(UserBuilder(), "User") as ArrayList<User>
            if(PR_UPD_USER(array)){
                for(obj in array){
                    obj.sql = true
                    FirebaseActions.createDocument(obj.uid, "User", ObjectMapper().convertValue(obj, object : TypeReference<Map<String, Any>>() {}))
                }
                Resource.Success(true)
            } else
                Resource.Success(false)
        }else
            Resource.Success(false)
    }

    fun PR_UPD_USER(jsonUsuarios: ArrayList<User>): Boolean =InstanceRetrofit.instanceRetrofit!!.create(UserService::class.java).upd_users(jsonUsuarios).execute().isSuccessful

    override suspend fun PR_UPD_JORNADAS(): Resource<Boolean> {
        return if(Network.connectedTo()){
            val array = builderArrayList(JornadaBuilder(), "Jornadas") as ArrayList<Jornada>
            if(PR_UPD_JORNADA(array)){
                for(jornada in array){
                    jornada.sql = true
                    FirebaseActions.createDocument(jornada.uid, "Jornadas", ObjectMapper().convertValue(jornada, object : TypeReference<Map<String, Any>>() {}))
                }
                Resource.Success(true)
            } else
                Resource.Success(false)
        }else
            Resource.Success(false)
    }

    fun PR_UPD_JORNADA(jsonJornadas : ArrayList<Jornada>) = InstanceRetrofit.instanceRetrofit!!.create(JornadaService::class.java).upd_jornada(jsonJornadas).execute().isSuccessful

    override suspend fun DEL_Receso(): Resource<Boolean> {
        val array = ArrayList<String>()
        val querySnapshot = FirebaseActions.getAllCollectionSQL("Motivos")
        for (change in querySnapshot.documentChanges) {
            if (change.type == DocumentChange.Type.ADDED) {
                MessageBuilder.sourceOrServer(querySnapshot, TAG)
                array.add(MotivoBuilder.motivoBuilder(change).uid)
            }
        }
        if(PR_DEL_RECESOS(array))
            return Resource.Success(UPD_MAN_RECESOS())
        return Resource.Success(false)
    }

    fun PR_DEL_RECESOS(documento: ArrayList<String>) =  InstanceRetrofit.instanceRetrofit!!.create(MotivoService::class.java).del_motivo(documento).execute().isSuccessful

    suspend fun UPD_MAN_RECESOS() : Boolean{
        return if(Network.connectedTo()){
            val array = builderArrayList(RecesoBuilder(), "Recesos") as ArrayList<Receso>
            if(PR_UPD_RECESOS(array)){
                for(obj in array){
                    obj.sql = true
                    FirebaseActions.createDocument(obj.uid, "Recesos", ObjectMapper().convertValue(obj, object : TypeReference<Map<String, Any>>() {}))
                }
                true
            } else
                false
        }else
            false
    }

    fun PR_UPD_RECESOS(jsonReceso: ArrayList<Receso>) =  InstanceRetrofit.instanceRetrofit!!.create(RecesoService::class.java).upd_receso(jsonReceso).execute().isSuccessful

    override suspend fun PR_UPD_MAN_OPERARIOS(): Resource<Boolean> {
        return if(Network.connectedTo()){
            val array = builderArrayList(OperarioBuilder(), "Operarios") as ArrayList<Operario>
            if(PR_UPD_OPERARIOS(array)){
                for(obj in array){
                    obj.sql = true
                    FirebaseActions.createDocument(obj.uid.toString(), "Operarios", ObjectMapper().convertValue(obj, object : TypeReference<Map<String, Any>>() {}))
                }
                Resource.Success(true)
            } else
                Resource.Success(false)
        }else
            Resource.Success(false)
    }

    fun PR_UPD_OPERARIOS(jsonOperarios : ArrayList<Operario>) = InstanceRetrofit.instanceRetrofit!!.create(OperarioService::class.java).upd_operario(jsonOperarios).execute().isSuccessful

    override suspend fun PR_UPD_MAN_OPERARIOS_LINEA(): Resource<Boolean> {
        return if(Network.connectedTo()){
            val array = builderArrayList(OperarioLineaBuilder(), "OperariosEnLinea") as ArrayList<OperarioLinea>
            if(PR_UPD_OPERARIOS_LINEA(array)){
                for(obj in array){
                    obj.sql = true
                    FirebaseActions.createDocument(obj.uid, "OperariosEnLinea", ObjectMapper().convertValue(obj, object : TypeReference<Map<String, Any>>() {}))
                }
                Resource.Success(true)
            } else
                Resource.Success(false)
        }else
            Resource.Success(false)
    }

    fun PR_UPD_OPERARIOS_LINEA(jsonOperariosLinea : ArrayList<OperarioLinea>) = InstanceRetrofit.instanceRetrofit!!.create(OperarioLineaService::class.java).upd_operariosLinea(jsonOperariosLinea).execute().isSuccessful

    override suspend fun PR_UPD_PRO_TALLOS_ASIGNADOS(): Resource<Boolean> {
        return if(Network.connectedTo()){
            val array = builderArrayList(TallosAsignadosBuilder(), "TallosAsignados") as ArrayList<TallosAsignados>
            if(PR_UPD_TALLOS_ASIGNADOS(array)){
                for(tallosAsignados in array){
                    tallosAsignados.sql = true
                    FirebaseActions.createDocument(tallosAsignados.uid, "TallosAsignados", ObjectMapper().convertValue(tallosAsignados, object : TypeReference<Map<String, Any>>() {}))
                }
                Resource.Success(true)
            } else
                Resource.Success(false)
        }else
            Resource.Success(false)
    }

    fun PR_UPD_TALLOS_ASIGNADOS(jsonTallosAsignados: ArrayList<TallosAsignados>) =
        InstanceRetrofit.instanceRetrofit!!.create(TallosAsignadosService::class.java).upd_tallosAsignados(jsonTallosAsignados).execute().isSuccessful

    override suspend fun PR_UPD_PRO_TALLOS_DESASIGNADOS(): Resource<Boolean> {
        return if(Network.connectedTo()){
            val array = builderArrayList(TallosDesasignadosBuilder(), "TallosDesasignados") as ArrayList<TallosDesasignados>
            if(PR_UPD_TALLOS_DESASIGNADOS(array)){
                for(tallosDesasignados in array){
                    tallosDesasignados.sql = true
                    FirebaseActions.createDocument(tallosDesasignados.uid, "TallosDesasignados", ObjectMapper().convertValue(tallosDesasignados, object : TypeReference<Map<String, Any>>() {}))
                }
                Resource.Success(true)
            } else
                Resource.Success(false)
        }else
            Resource.Success(false)
    }

    fun PR_UPD_TALLOS_DESASIGNADOS(jsonTallosDesasignados : ArrayList<TallosDesasignados>) =
    InstanceRetrofit.instanceRetrofit!!.create(TallosDesasignadosService::class.java).upd_tallosDesasignados(jsonTallosDesasignados).execute().isSuccessful

    override suspend fun PR_UPD_PRO_CIERRES_LINEA(): Resource<Boolean> {
        return if(Network.connectedTo()){
            val array = builderArrayList(CierreLineaBuilder(), "CierreLinea") as ArrayList<CierreLinea>
            if(PR_UPD_CIERRE_LINEA(array)){
                for(cierreLinea in array){
                    cierreLinea.sql = true
                    FirebaseActions.createDocument(cierreLinea.uid, "CierreLinea", ObjectMapper().convertValue(cierreLinea, object : TypeReference<Map<String, Any>>() {}))
                }
                Resource.Success(true)
            } else
                Resource.Success(false)
        }else
            Resource.Success(false)
    }

    fun PR_UPD_CIERRE_LINEA(jsonCierreLinea: ArrayList<CierreLinea>): Boolean =
        InstanceRetrofit.instanceRetrofit!!.create(CierreLineaService::class.java).upd_cierre_linea(jsonCierreLinea).execute().isSuccessful

    override suspend fun PR_UPD_PRO_CIERRES_OPERARIO(): Resource<Boolean> {
        return if(Network.connectedTo()){
            val array = builderArrayList(CierreOperarioBuilder(), "CierreOperario") as ArrayList<CierreOperario>
            if(PR_UPD_PRO_CIERRES_OPERARIO(array)){
                for(cierreOperario in array){
                    cierreOperario.sql = true
                    FirebaseActions.createDocument(cierreOperario.uid, "CierreOperario", ObjectMapper().convertValue(cierreOperario, object : TypeReference<Map<String, Any>>() {}))
                }
                Resource.Success(true)
            } else
                Resource.Success(false)
        }else
            Resource.Success(false)
    }

    fun PR_UPD_PRO_CIERRES_OPERARIO(jsonCierreOperario: ArrayList<CierreOperario>): Boolean =
        InstanceRetrofit.instanceRetrofit!!.create(CierreOperarioService::class.java).upd_cierre_operario(jsonCierreOperario).execute().isSuccessful

    override suspend fun PR_UPD_MAN_RENDIMIENTO(): Resource<Boolean> {
        return if(Network.connectedTo()){
            val array = builderArrayList(RendimientoBuilder(), "Rendimiento") as ArrayList<Rendimiento>
            val arrayValidos = ArrayList<Rendimiento>()
            array.forEach { if (it.uid!="Finca0Linea0") arrayValidos.add(it)}
            if(PR_UPD_MAN_RENDIMIENTO(arrayValidos)){
                for(rendimiento in arrayValidos){
                    rendimiento.sql = true
                    FirebaseActions.createDocument(rendimiento.uid, "Rendimiento", ObjectMapper().convertValue(rendimiento, object : TypeReference<Map<String, Any>>() {}))
                }
                Resource.Success(true)
            } else
                Resource.Success(false)
        } else
            Resource.Success(false)
    }

    fun PR_UPD_MAN_RENDIMIENTO(jsonRendimiento: ArrayList<Rendimiento>): Boolean =
        InstanceRetrofit.instanceRetrofit!!.create(RendimientoService::class.java).upd_rendimiento(jsonRendimiento).execute().isSuccessful

    override suspend fun PR_UPD_ALL_SQL(sql: Boolean): Resource<String> {
        val fincas = builderArrayListAll(FincaBuilder(), "Fincas") as ArrayList<Finca>
        fincas.forEach {
            it.sql = sql
            PR_UPD_ALL_COLLECTION("Fincas", it.uid.toString(), it)
        }

        val lineas = builderArrayListAll(LineaBuilder(), "Lineas") as ArrayList<Linea>
        lineas.forEach {
            it.sql = sql
            PR_UPD_ALL_COLLECTION("Lineas", it.uid.toString(), it)
        }
        val operarios = builderArrayListAll(OperarioBuilder(), "Operarios") as ArrayList<Operario>
        operarios.forEach { it.sql = sql
            PR_UPD_ALL_COLLECTION("Operarios", it.uid.toString(), it)
        }
        val permisos = builderArrayListAll(PermisoBuilder(), "Permisos") as ArrayList<Permiso>
        permisos.forEach { it.sql = sql
            PR_UPD_ALL_COLLECTION("Permisos", it.uid, it)
        }
        val rendimiento = builderArrayListAll(RendimientoBuilder(), "Rendimiento") as ArrayList<Rendimiento>
        rendimiento.forEach {
            it.sql = sql
            PR_UPD_ALL_COLLECTION("Rendimiento", it.uid, it)
        }
        val usuarios = builderArrayListAll(UserBuilder(), "User") as ArrayList<User>
        usuarios.forEach { it.sql = sql
            PR_UPD_ALL_COLLECTION("User", it.uid, it)
        }
        return Resource.Success("Variables modificadas en: $sql" )
    }

    fun PR_UPD_ALL_COLLECTION(collection: String, id: String, it: Any){
        FirebaseActions.createDocument(id, collection,
            ObjectMapper().convertValue(it, object : TypeReference<Map<String, Any>>() {}))
    }
}