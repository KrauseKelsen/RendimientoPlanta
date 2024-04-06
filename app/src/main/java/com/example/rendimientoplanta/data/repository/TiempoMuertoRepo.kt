package com.example.rendimientoplanta.data.repository
import com.example.rendimientoplanta.base.builder.*
import com.example.rendimientoplanta.base.pojos.Operario
import com.example.rendimientoplanta.base.pojos.TallosAsignados
import com.example.rendimientoplanta.base.pojos.TiempoMuerto
import com.example.rendimientoplanta.base.pojos.User
import com.example.rendimientoplanta.base.utils.instances.FirebaseActions
import com.example.rendimientoplanta.base.utils.instances.Network
import com.example.rendimientoplanta.base.utils.validators.CalcuTimeValidators
import com.example.rendimientoplanta.data.irepository.ITiempoMuertoRepo
import com.example.rendimientoplanta.pattern.interprete.Contexto
import com.example.rendimientoplanta.vo.Resource
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TiempoMuertoRepo : ITiempoMuertoRepo {
    private val TAG = "TiempoMuertoRepo"

    private var contextoInicio = Contexto()
    private var contextoFin = Contexto()
    private var contextoTallosInicio = Contexto()
    private var contextoTallosFin = Contexto()
    private var tallosAsignados = ArrayList<TallosAsignados>()
    private var respuesta = 0
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

    override suspend fun GET_Operarios(user: User): Resource<ArrayList<Operario>> {
        return Resource.Success(builderArrayList(OperarioBuilder(),
            FirebaseActions.getColletionWhereEqualTo("Operarios", "estado", true)) as ArrayList<Operario>)
    }

    fun llenarContextos(horaInicio: String, horaFin: String){
        contextoInicio.setHoraDoce(horaInicio, contextoInicio)
        contextoFin.setHoraDoce(horaFin, contextoFin)
    }

    override suspend fun SET_TiempoMuerto(operario: Operario, user: User, horaInicio: String, horaFin: String, motivo: String): Resource<String> {
        var resultado = ""
        llenarContextos(horaInicio, horaFin)
        resultado = if(operario.ocupado)
            operarioOcupado(operario)
        else
            operarioDesocupado(operario)

        if(respuesta == 0){
            val tiempoMuerto = TiempoMuertoBuilder().buildTiempoMuerto(operario, user, horaFin, horaInicio, motivo)

            val array = ArrayList<TiempoMuerto>()
            array.add(tiempoMuerto)
            tiempoMuerto.sql = if(Network.connectedTo()) PR_UPD_TIEMPO_MUERTO(array) else false
            FirebaseActions.createDocument(tiempoMuerto.uid, "TiempoMuerto", ObjectMapper()
                .convertValue(tiempoMuerto, object : TypeReference<Map<String, Any>>() {})).isSuccessful
        }

        return Resource.Success("$resultado")

    }

    private suspend fun operarioOcupado(operario: Operario): String {
        tallosAsignados = getTallosAsignadosAbiertos(true, operario)
        val contextoTallos = Contexto()
        //hay que validar que no choque con otra cita
        if(tallosAsignados.size==0) return operarioDesocupado(operario)
        else {
            contextoTallos.setHoraVeintiCuatro(tallosAsignados[0].horaInicio, contextoTallos)
        }
        return if(CalcuTimeValidators.AwhereGreaterThanB(tallosAsignados[0].horaInicio, contextoFin.hora24))
            operarioDesocupado(operario) //hay que validar que no choque con otra cita
        else
            devolverMensaje(2, tallosAsignados[0].tallos, "${contextoTallos.hora12} ${contextoTallos.AMPM}", "${contextoTallos.hora12} ${contextoTallos.AMPM}")
    }

    private suspend fun operarioDesocupado(operario: Operario): String {
        tallosAsignados = getTallosAsignadosAbiertos(false, operario)
        for (j in tallosAsignados.indices) {
            if(CalcuTimeValidators.AwhereGreaterThanB(tallosAsignados[j].horaInicio, contextoInicio.hora24)
                && (CalcuTimeValidators.AwhereGreaterThanB(tallosAsignados[j].horaInicio, contextoFin.hora24)
                        && CalcuTimeValidators.AwhereGreaterThanB(tallosAsignados[j].horaFin, contextoFin.hora24))){
                return devolverMensaje(0, 0, "", "")
            }

            if(CalcuTimeValidators.AwhereGreaterThanB(contextoInicio.hora24, tallosAsignados[j].horaInicio)
                    && CalcuTimeValidators.AwhereGreaterThanToEqualB(tallosAsignados[j].horaFin, contextoInicio.hora24)){
                return devolverMensaje(1, tallosAsignados[j].tallos, tallosAsignados[j].horaInicio, tallosAsignados[j].horaFin)
            }

            if((CalcuTimeValidators.AwhereGreaterThanB(contextoInicio.hora24, tallosAsignados[j].horaInicio)
                || CalcuTimeValidators.AwhereGreaterThanB(tallosAsignados[j].horaInicio, contextoInicio.hora24))
                && CalcuTimeValidators.AwhereGreaterThanB(tallosAsignados[j].horaFin, contextoFin.hora24)){
                return devolverMensaje(1, tallosAsignados[j].tallos, tallosAsignados[j].horaInicio, tallosAsignados[j].horaFin)
            }

            if(CalcuTimeValidators.AwhereGreaterThanB(tallosAsignados[j].horaInicio, contextoInicio.hora24)
                && CalcuTimeValidators.AwhereGreaterThanB(contextoFin.hora24, tallosAsignados[j].horaFin)){
                return devolverMensaje(1, tallosAsignados[j].tallos, tallosAsignados[j].horaInicio, tallosAsignados[j].horaFin)
            }
        }
        return devolverMensaje(0, 0, "", "")
    }

    suspend fun getTallosAsignadosAbiertos(estado: Boolean, operario: Operario) =
            builderArrayList(TallosAsignadosBuilder(),
                FirebaseActions.getColletionWhereEqualTo("TallosAsignados", "estado", estado,
                    "fecha", SimpleDateFormat("yyyy-MM-dd").format(Date()), "operarioId", operario.uid)) as ArrayList<TallosAsignados>

    fun PR_UPD_TIEMPO_MUERTO(jsonTiempoMuerto : ArrayList<TiempoMuerto>) =  false

    fun devolverMensaje(mensaje: Int, tallos: Int, horaInicioTallosAsignados: String, horaFinTallosAsignados: String): String {
        respuesta = mensaje
        if(mensaje!=0) iniciarHorasTallos(horaInicioTallosAsignados, horaFinTallosAsignados)
        return when(mensaje){
            0 -> "Se ha registrado un tiempo muerto el día de hoy de ${contextoInicio.hora12} a ${contextoFin.hora12} de manera exitosa."
            1 -> "Las horas del tiempo muerto ingresadas de ${contextoInicio.hora12} a ${contextoFin.hora12} " +
                    "interfieren con una asignación de $tallos tallos realizada el día de hoy de " +
                    "${contextoTallosInicio.hora12} ${contextoTallosInicio.AMPM} a ${contextoTallosFin.hora12} ${contextoTallosFin.AMPM}" +
                    "\nPor favor ingrese un intervalo de horas que no interfieran con las asignaciones de hoy."
            2 -> "El operario se encuentra ocupado ya que se le asignaron $tallos tallos a las ${contextoTallosInicio.hora24}" +
                    "\nPor favor desasignele los tallos al operario para poder registrar un tiempo muerto de " +
                    "${contextoInicio.hora12} a ${contextoFin.hora12}"
            else -> ""
        }
    }

    fun iniciarHorasTallos(horaInicioTallosAsignados: String, horaFinTallosAsignados: String) {
        contextoTallosInicio.setHoraVeintiCuatro(horaInicioTallosAsignados, contextoTallosInicio)
        contextoTallosFin.setHoraVeintiCuatro(horaFinTallosAsignados, contextoTallosFin)
    }
}