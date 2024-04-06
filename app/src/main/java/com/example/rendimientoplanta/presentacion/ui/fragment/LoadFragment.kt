package com.example.rendimientoplanta.presentacion.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.utils.instances.Network
import com.example.rendimientoplanta.data.repository.LoadRepo
import com.example.rendimientoplanta.domain.impldomain.LoadCase
import com.example.rendimientoplanta.presentacion.factory.LoadFactory
import com.example.rendimientoplanta.presentacion.viewmodel.LoadVM
import com.example.rendimientoplanta.vo.Resource
import kotlinx.android.synthetic.main.fragment_load.*

/**
 * LoadFragment: El fragmento encargado de cargar las colecciones de Firebase a SQL al iniciar la aplicación
 * BaseFragment: El fragmento encargado de gestionar los permisos y variables globales de la aplicación
 */
class LoadFragment : BaseFragment(false, false, "Load"){
    private val TAG = "LoadFragment"
    // ViewModel toma la información del fragment y la envía al UseCase y el Repository mediante el Factory (revisar carpetas)
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            LoadFactory(LoadCase(LoadRepo()))
        ).get(LoadVM::class.java)
    }

    // Inicia el xml del fragmento
    override fun getViewID():Int = R.layout.fragment_load
    // Si el if es true, entonces se desvinculan los datos (todas las colecciones quedan con su variable sql false)
    // esto con el fin de que sean sincronizadas hacia sql cada vez que se abre el app
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(true) desvincularDatos(false) else ejecutarSincronizacion()
    }

    //Mediante el View Model utiliza la variable sql que se envía por parametro
    private fun desvincularDatos(sql: Boolean) {
        viewModel.updall(sql).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    //Mientras los datos llegan se muestra este mensaje en la pantallaThe Eyes of Darkness
                    messageFragment(TAG, "Actualizando todo...", tvLoading, "Sincronizando datos...")
                }
                is Resource.Success -> {
                    messageFragment(TAG, "${result.data}", tvLoading, "Datos sincronizados...")
                    //Una vez todas las colecciones quedan con su variable en sql false se ejecuta la sincronización
                    ejecutarSincronizacion()
                }
                is Resource.Failure -> {
                    messageFragment(TAG, "Error no se pudieron actualizar las colecciones; Motivo__: ${result.exception.message}",
                        tvLoading, "No se pudieron sincronizar...\nActualizando información...")
                    ejecutarSincronizacion()
                }
            }
        })
    }

    // Esta función pregunta si existe conexión con el servidor haciendo uso de la clase Network de la carpeta base.utils.instances
    private fun ejecutarSincronizacion() {
        //Si hay conexión con el servidor comienza a cargar los datos de firebase a SQL
        if (Network.connectedTo()) {
            //DialogMessage(context,this,"Se ha verificado una conexión estable con el servidor, ¿desea sincronizar los datos no subidos antes de continuar?")
            cargarDatosSQL()
        } else {
            //Si no hay conexión con el servidor inicia el Start Fragment
            tvLoading.text = "Sin conexión con el servidor..."
            findNavController().navigate(R.id.startFragment) // redirecciona al startfragment
        }
    }

    private fun cargarDatosSQL() {
        cargarPermisosToSQL()
    }
    fun cargarPermisosToSQL() {
        viewModel.updPermisos().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    messageFragment(TAG, "Actualizando permisos...", tvLoading, "Actualizando permisos...")
                }
                is Resource.Success -> {
                    messageFragment(TAG, "Permisos actualizados en SQL ${result.data}", tvLoading, "Permisos actualizados...")
                    cargarFincasToSQL()
                }
                is Resource.Failure -> {
                    messageFragment(TAG, "Error no se pudieron actualizar los permisos; Motivo__: ${result.exception.message}",
                    tvLoading, "Error al actualizar los permisos...")
                    cargarFincasToSQL()
                }
            }
        })
    }

    fun cargarFincasToSQL() {
        viewModel.updFincas().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    messageFragment(TAG, "Actualizando fincas...", tvLoading, "Actualizando fincas...")
                }
                is Resource.Success -> {
                    messageFragment(TAG, "Fincas actualizadas en SQL ${result.data}", tvLoading, "Fincas actualizadas...")
                    cargarLineasToSQL()
                }
                is Resource.Failure -> {
                    messageFragment(TAG, "Error no se pudieron actualizar las fincas; Motivo__: ${result.exception.message}",
                        tvLoading, "Error al actualizar las fincas...")
                    cargarLineasToSQL()
                }
            }
        })
    }

    fun cargarLineasToSQL() {
        viewModel.updLineas().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    messageFragment(TAG, "Actualizando líneas...", tvLoading, "Actualizando líneas...")
                }
                is Resource.Success -> {
                    messageFragment(TAG, "Líneas actualizadas en SQL ${result.data}", tvLoading, "Líneas actualizadas...")
                    cargarUsuariosToSQL()
                }
                is Resource.Failure -> {
                    messageFragment(TAG, "Error no se pudieron actualizar las líneas; Motivo__: ${result.exception.message}",
                        tvLoading, "Error al actualizar las líneas...")
                    cargarUsuariosToSQL()
                }
            }
        })
    }

    fun cargarUsuariosToSQL() {
        viewModel.updUsers().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    messageFragment(TAG, "Actualizando usuarios...", tvLoading, "Actualizando usuarios...")
                }
                is Resource.Success -> {
                    messageFragment(TAG, "Usuarios actualizados en SQL ${result.data}", tvLoading, "Usuarios actualizados...")
                    cargarJornadasToSQL()
                }
                is Resource.Failure -> {
                    messageFragment(TAG, "Error no se pudieron actualizar los usuarios; Motivo__: ${result.exception.message}",
                        tvLoading, "Error al actualizar los usuarios...")
                    cargarJornadasToSQL()
                }
            }
        })
    }

    fun cargarJornadasToSQL() {
        viewModel.updJornadas().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    messageFragment(TAG, "Actualizando jornadas...", tvLoading, "Actualizando jornadas...")
                }
                is Resource.Success -> {
                    messageFragment(TAG, "Jornadas actualizadas en SQL ${result.data}", tvLoading, "Jornadas actualizadas...")
                    borrarMotivosToSQL()
                }
                is Resource.Failure -> {
                    messageFragment(TAG, "Error no se pudieron actualizar las jornadas; Motivo__: ${result.exception.message}",
                        tvLoading, "Error al actualizar las jornadas...")
                    borrarMotivosToSQL()
                }
            }
        })
    }

    fun borrarMotivosToSQL() {
        viewModel.delReceso().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    messageFragment(TAG, "Actualizando recesos...", tvLoading, "Actualizando recesos...")
                }
                is Resource.Success -> {
                    messageFragment(TAG, "Recesos actualizados en SQL ${result.data}", tvLoading, "Recesos actualizados...")
                    cargarOperariosToSQL()
                }
                is Resource.Failure -> {
                    messageFragment(TAG, "Error no se pudieron actualizar los recesos; Motivo__: ${result.exception.message}",
                        tvLoading, "Error al actualizar los recesos...")
                    cargarOperariosToSQL()
                }
            }
        })
    }

    fun cargarOperariosToSQL() {
        viewModel.updOperarios().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    messageFragment(TAG, "Actualizando operarios...", tvLoading, "Actualizando operarios...")
                }
                is Resource.Success -> {
                    messageFragment(TAG, "Operarios actualizados en SQL ${result.data}", tvLoading, "Operarios actualizados...")
                    cargarOperariosLineaToSQL()
                }
                is Resource.Failure -> {
                    messageFragment(TAG, "Error no se pudieron actualizar los operarios; Motivo__: ${result.exception.message}",
                        tvLoading, "Error al actualizar los operarios...")
                    cargarOperariosLineaToSQL()
                }
            }
        })
    }

    fun cargarOperariosLineaToSQL() {
        viewModel.updOperariosLinea().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    messageFragment(TAG, "Actualizando operarios en línea...", tvLoading, "Actualizando operarios en línea...")
                }
                is Resource.Success -> {
                    messageFragment(TAG, "Operarios en línea actualizados en SQL ${result.data}", tvLoading, "Operarios en línea actualizados...")
                    cargarTallosAsignadosToSQL()
                }
                is Resource.Failure -> {
                    messageFragment(TAG, "Error no se pudieron actualizar los operarios en línea; Motivo__: ${result.exception.message}",
                        tvLoading, "Error al actualizar los operarios en línea...")
                    cargarTallosAsignadosToSQL()
                }
            }
        })
    }

    fun cargarTallosAsignadosToSQL() {
        viewModel.updTallosAsignados().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    messageFragment(TAG, "Actualizando tallos asignados...", tvLoading, "Actualizando tallos asignados...")
                }
                is Resource.Success -> {
                    messageFragment(TAG, "Tallos asignados actualizados en SQL ${result.data}", tvLoading, "Tallos asignados actualizados...")
                    cargarTallosDesasignadosToSQL()
                }
                is Resource.Failure -> {
                    messageFragment(TAG, "Error no se pudieron actualizar los tallos asignados; Motivo__: ${result.exception.message}",
                        tvLoading, "Error al actualizar los tallos asignados...")
                    cargarTallosDesasignadosToSQL()
                }
            }
        })
    }

    fun cargarTallosDesasignadosToSQL() {
        viewModel.updTallosDesasignados().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    messageFragment(TAG, "Actualizando tallos desasignados...", tvLoading, "Actualizando tallos desasignados...")
                }
                is Resource.Success -> {
                    messageFragment(TAG, "Tallos desasignados actualizados en SQL ${result.data}", tvLoading, "Tallos desasignados actualizados...")
                    cargarCierresLineaToSQL()
                }
                is Resource.Failure -> {
                    messageFragment(TAG, "Error no se pudieron actualizar los tallos desasignados; Motivo__: ${result.exception.message}",
                        tvLoading, "Error al actualizar los tallos desasignados...")
                    cargarCierresLineaToSQL()
                }
            }
        })
    }

    fun cargarCierresLineaToSQL() {
        viewModel.updCierresLinea().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    messageFragment(TAG, "Actualizando cierres de línea...", tvLoading, "Actualizando cierres de línea...")
                }
                is Resource.Success -> {
                    messageFragment(TAG, "Cierres de línea actualizados en SQL ${result.data}", tvLoading, "Cierres de línea actualizados...")
                    cargarCierresOperarioToSQL()
                }
                is Resource.Failure -> {
                    messageFragment(TAG, "Error no se pudieron actualizar los cierres de línea; Motivo__: ${result.exception.message}",
                        tvLoading, "Error al actualizar los cierres de línea...")
                    cargarCierresOperarioToSQL()
                }
            }
        })
    }

    fun cargarCierresOperarioToSQL() {
        viewModel.updCierresOperario().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    messageFragment(TAG, "Actualizando cierres de operario...", tvLoading, "Actualizando cierres de operario...")
                }
                is Resource.Success -> {
                    messageFragment(TAG, "Cierres de operario actualizados en SQL ${result.data}", tvLoading, "Cierres de operario actualizados...")
                    cargarRendimientoToSQL()
                }
                is Resource.Failure -> {
                    messageFragment(TAG, "Error no se pudieron actualizar los cierres de operario; Motivo__: ${result.exception.message}",
                        tvLoading, "Error al actualizar los cierres de operario...")
                    cargarRendimientoToSQL()
                }
            }
        })
    }

    fun cargarRendimientoToSQL() {
        viewModel.updRendimiento().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    messageFragment(TAG, "Actualizando rendimientos...", tvLoading, "Actualizando rendimientos...")
                }
                is Resource.Success -> {
                    messageFragment(TAG, "Rendimientos actualizados en SQL ${result.data}", tvLoading, "Rendimientos actualizados...")
                    findNavController().navigate(R.id.startFragment)
                }
                is Resource.Failure -> {
                    messageFragment(TAG, "Error no se pudieron actualizar los rendimientos; Motivo__: ${result.exception.message}",
                        tvLoading, "Error al actualizar los rendimientos...")
                    findNavController().navigate(R.id.startFragment)
                }
            }
        })
    }
}