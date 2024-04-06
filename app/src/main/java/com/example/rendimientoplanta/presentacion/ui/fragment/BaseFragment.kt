package com.example.rendimientoplanta.presentacion.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.builder.MessageBuilder
import com.example.rendimientoplanta.base.builder.StringBuilder
import com.example.rendimientoplanta.base.pojos.*
import com.example.rendimientoplanta.base.utils.instances.BundleActivity
import com.example.rendimientoplanta.base.utils.validators.FieldLoginValidators
import com.example.rendimientoplanta.base.utils.validators.FieldValidators
import com.example.rendimientoplanta.data.repository.BaseRepo
import com.example.rendimientoplanta.domain.impldomain.BaseCase
import com.example.rendimientoplanta.presentacion.factory.BaseFactory
import com.example.rendimientoplanta.presentacion.ui.dialogs.DialogConfig
import com.example.rendimientoplanta.presentacion.ui.dialogs.DialogMessage
import com.example.rendimientoplanta.presentacion.viewmodel.BaseVM
import com.example.rendimientoplanta.vo.Resource
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_config.*
import kotlinx.android.synthetic.main.dialog_config.textInputLayout_password
import kotlinx.android.synthetic.main.dialog_config.text_password
import kotlinx.android.synthetic.main.fragment_load.*
import kotlinx.android.synthetic.main.toolbar.*
import java.lang.NumberFormatException
import kotlin.collections.ArrayList

abstract class BaseFragment(bn_visible: Boolean, cl_visible: Boolean, subtitle: String) : Fragment(), DialogMessage.FinalizoDialog , DialogConfig.FinalizoDialog {
    private val TAG = "BaseFragment"

    private var pbn_visible = bn_visible
    private var pcl_visible = cl_visible
    private var psubtitle = subtitle
    lateinit var user : User
    //lateinit var jornadaBase : Jornada
    lateinit var rendimiento : Rendimiento
    var dispositivo: String = ""
    lateinit var permisos: Permiso
    lateinit var fincas :  ArrayList<String>
    lateinit var lineas : ArrayList<String>

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            BaseFactory(BaseCase(BaseRepo()))
        ).get(BaseVM::class.java)
    }

    protected abstract fun getViewID():Int

    fun messageFragment(TAG: String, textLog: String, tvLoading: TextView, textLoading: String){
        Log.w(TAG, textLog)
        tvLoading.text = textLoading
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        initDispositivo() // inicializa el dispositivo que usa el app
        verificarTokenCompilacion() // verifica el token de compilacion y al terminar verifica los permisos del usuario en tiempo de compilacion
        //sumarNotify(requireActivity().intent.getStringExtra("Nube")!!.toInt())
        user = requireActivity().intent.getParcelableExtra("User")!! // setea el usuario que está en la bolsa
        rendimiento = requireActivity().intent.getParcelableExtra("Rendimiento")!! // setea el rendimiento que está en la bolsa
        //jornada = requireActivity().intent.getParcelableExtra("Jornada")!! // setea jornada que está en la bolsa
        permisos = requireActivity().intent.getParcelableExtra("Permisos")!! // setean los permisos que está en la bolsa
        visibleItems() // dependiendo del fragmento que lo inicializa deberá mostrar o no mostrar los items
        visibleConfig() // si el fragmento es procesos, producto,cierre o reporte muestra el boton de configuracion, si no lo deja oculto
        requireActivity().btnBack.setOnClickListener { onBackClick() } // si pronuncian el botón de atrás
        requireActivity().layoutCloud.setOnClickListener {findNavController().navigate(R.id.loadFragment)} // si se presiona la nube se redirecciona al loadfragment
        requireActivity().btnConfig.setOnClickListener {  getFincas() }
        return inflater.inflate(getViewID(), container, false)
    }

    @SuppressLint("HardwareIds")
    fun initDispositivo(){
        if (dispositivo=="") dispositivo = Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
    }

    private fun verificarTokenCompilacion() {
        if (verificarFragment()) {
            getToken()
        }
    }

    private fun verificarFragment() : Boolean{
        return (getViewID() == R.layout.fragment_procesos || getViewID() == R.layout.fragment_producto
                || getViewID() == R.layout.fragment_cierre || getViewID() == R.layout.fragment_reporte
                || getViewID() == R.layout.fragment_opciones)
    }

    private fun getToken() {
        viewModel.getToken(dispositivo).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.w(TAG, "${MessageBuilder.sLoading} Obteniendo usuario logeado por medio del dispositivo $dispositivo")
                }
                is Resource.Success -> {
                    BundleActivity.updateBundle(requireActivity(), result.data)
                    user = requireActivity().intent.getParcelableExtra("User")!!
                    Log.w(TAG, "Usuario obtenido logeado $user")
                    getRendimiento()
                }
                is Resource.Failure -> {
                    //si se pierde el token, o se deshabilita el usuario en medio de la ejecución del app, simplemente el app sale
                    findNavController().navigate(R.id.loginFragment)
                }
            }
        })
    }

    private fun getRendimiento(){
        viewModel.getRendimiento(user).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.w(TAG, "Obteniendo el rendimiento...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Rendimiento obtenido ${result.data}")
                    BundleActivity.updateBundle(requireActivity(), result.data)
                    rendimiento = requireActivity().intent.getParcelableExtra("Rendimiento")!!
                    verificarPermisosCompilacion()
                }
                is Resource.Failure -> {
                    when(result.exception){
                        is NumberFormatException -> {
                            updRendimiento(0, true)
                        }
                        else -> findNavController().navigate(R.id.loginFragment)
                    }
                }
            }
        })
    }

    private fun verificarPermisosCompilacion() {
        if (verificarFragment()) {
            getPermisos()
        }
    }

    private fun getPermisos() {
        permisos = requireActivity().intent.getParcelableExtra("Permisos")!!
        user = requireActivity().intent.getParcelableExtra("User")!!
        viewModel.getPermisos(user.rol).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.w(TAG, "Obteniendo permisos del usuario logeado...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Permisos obtenidos ${result.data}")
                    if(result.data.nombres!=permisos.nombres) {
                        BundleActivity.updateBundle(requireActivity(), result.data)
                        permisos = requireActivity().intent.getParcelableExtra("Permisos")!!
                        switchFrame(hideFrames("BaseFragment", permisos))
                    }
                }
                is Resource.Failure -> {
                    BundleActivity.updateBundle(requireActivity(), permisos)
                    permisos = requireActivity().intent.getParcelableExtra("Permisos")!!
                    switchFrame(hideFrames("BaseFragment", permisos))
                    //Si no se obtienen los permisos no se puede hacer nada mas que enviar los permisos vacios porque no se puede mostar un mensaje de error
                    Log.w(TAG, "${MessageBuilder.sFailure}, no se pudieron obtener los permisos del usuario")
                }
            }
        })
    }

    fun switchFrame(vista: Int) {
        when (vista) {
            0 -> findNavController().navigate(R.id.procesosFragment)
            1 -> findNavController().navigate(R.id.productoFragment)
            2 -> findNavController().navigate(R.id.cierreFragment)
            3 -> findNavController().navigate(R.id.reporteFragment)
            4 -> findNavController().navigate(R.id.opcionesFragment)
            else -> findNavController().navigate(R.id.loginFragment)
        }
    }

    fun hideFrames(fragment: String, permisos : Permiso) : Int {
        return if(permisos.nombres.size==0){
             -1
        }else{
            var vista = 0
            Log.w(TAG, "Fui llamado por $fragment")
            for (count in 0 until requireActivity().bottom_navigation.menu.size){
                requireActivity().bottom_navigation.menu[count].isVisible = false
            }
            //con el usuario logeado podemos revisar los permisos
            for(permiso in permisos.nombres){
                when (permiso) {
                    "Opciones" -> {
                        requireActivity().bottom_navigation.menu[4].isVisible = true
                        vista = 4
                    }
                    "Reportes" -> {
                        requireActivity().bottom_navigation.menu[3].isVisible = true
                        vista = 3
                    }
                    "Cierre" -> {
                        requireActivity().bottom_navigation.menu[2].isVisible = true
                        vista = 2
                    }
                    "Producto" -> {
                        requireActivity().bottom_navigation.menu[1].isVisible = true
                        vista = 1
                    }
                    "Procesos" -> {
                        requireActivity().bottom_navigation.menu[0].isVisible = true
                        vista = 0
                    }
                }
            }
            vista
        }
    }

    fun updRendimiento(valor: Int, bandera : Boolean) {
        updRendimiento(user, valor, bandera)
        cargarVista(user, "BaseFragment")
    }

    fun updRendimiento(user: User, valor: Int, bandera : Boolean){
        viewModel.updRendimiento(user, valor, rendimiento, bandera).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.w(TAG, "Obteniendo el rendimiento...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Rendimiento obtenido ${result.data}")
                    BundleActivity.updateBundle(requireActivity(), result.data)
                    rendimiento = requireActivity().intent.getParcelableExtra("Rendimiento")!!
                }
                is Resource.Failure -> {
                    when(result.exception){
                        is NumberFormatException -> updRendimiento(user, 0, true)
                    }
                    Log.w(TAG, "${MessageBuilder.sFailure}, no se pudo actualizar el rendimiento")
                }
            }
        })
    }

    fun cargarVista(user: User, fragment: String) {
        getPermisos(user, fragment)
    }

    private fun getPermisos(puser: User, fragment: String) {
        permisos = requireActivity().intent.getParcelableExtra("Permisos")!!
        user = requireActivity().intent.getParcelableExtra("User")!!
        viewModel.getPermisos(user.rol).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.w(TAG, "Obteniendo permisos del usuario logeado...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Permisos obtenidos ${result.data}")
                    BundleActivity.updateBundle(requireActivity(), result.data)
                    permisos = requireActivity().intent.getParcelableExtra("Permisos")!!
                    switchFrame(hideFrames(fragment, permisos))
                }
                is Resource.Failure -> {
                    BundleActivity.updateBundle(requireActivity(), permisos)
                    permisos = requireActivity().intent.getParcelableExtra("Permisos")!!
                    switchFrame(hideFrames("BaseFragment", permisos))
                    //Si no se obtienen los permisos no se puede hacer nada mas que enviar los permisos vacios porque no se puede mostar un mensaje de error
                    Log.w(TAG, "${MessageBuilder.sErrorLogin} no se pudieron obtener los permisos del usuario")
                }
            }
        })
    }

    private fun visibleItems() {
        requireActivity().toolbar.subtitle = psubtitle
        requireActivity().bottom_navigation.isVisible = pbn_visible
        requireActivity().constaintLayout_toolbar.isVisible = pcl_visible
        requireActivity().btnConfig.isVisible = false
    }

    private fun visibleConfig() {
        if (verificarFragment()) {
            requireActivity().btnConfig.isVisible = true
        }
    }

    private fun onBackClick() {
        when (getViewID()){
            R.layout.fragment_procesos, R.layout.fragment_producto, R.layout.fragment_cierre, R.layout.fragment_reporte, R.layout.fragment_opciones ->
                DialogMessage(context,this,"¿Desea cerrar sesión?. \rLa próxima vez que inicie la aplicación deberá ingresar sus credenciales")
            R.layout.fragment_asignar_producto, R.layout.fragment_desasignar_producto  -> findNavController().navigate(R.id.productoFragment)
            R.layout.fragment_recesos, R.layout.fragment_jornada, R.layout.fragment_operario  -> findNavController().navigate(R.id.procesosFragment)
            R.layout.fragment_cierre_operario, R.layout.fragment_cierre_linea  -> findNavController().navigate(R.id.cierreFragment)
            R.layout.fragment_sacar_linea -> findNavController().navigate(R.id.operarioFragment)
        }
    }

    override fun resultadoYesDialog(dialog: Dialog?) {
        dialog?.dismiss()
        findNavController().navigate(R.id.logoutFragment)
    }

    override fun resultadoNoDialog(dialog: Dialog?) {
        dialog?.dismiss()
    }

    fun getFincas(){
        requireActivity().btnConfig.isClickable = false
        viewModel.getFincas().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.w(TAG, "Obteniendo fincas...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Fincas obtenidas ${result.data}")
                    fincas = StringBuilder.getStrings(result.data as ArrayList<Any>, "nombre")
                    getLineas()
                }
                is Resource.Failure -> {
                    Log.w(TAG, "${MessageBuilder.sFailure}, no se pudieron obtener las fincas")
                }
            }
        })
    }

    fun getLineas(){
        viewModel.getLineas().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.w(TAG, "Obteniendo lineas...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Lineas obtenidas ${result.data}")
                    lineas = StringBuilder.getStrings(result.data as ArrayList<Any>, "nombre")
                    DialogConfig(context,this,  fincas, lineas, user, rendimiento)
                    requireActivity().btnConfig.isClickable = true
                }
                is Resource.Failure -> {
                    Log.w(TAG, "${MessageBuilder.sFailure}, no se pudieron obtener las lineas")
                }
            }
        })
    }

    override fun resultadoConfigNoDialog(dialog: Dialog?) {
        dialog?.dismiss()
        Log.e(TAG, user.toString())
    }

    override fun resultadoConfigYesDialog(dialog: Dialog?, finca: String, linea: Int, rendimientoInt: Int) {
        if(dialog!!.text_changePassword.text.toString()==""){
            getSeguridad(dialog, finca, linea, false)
        }else{
            if(FieldLoginValidators.validatePassword(dialog.textInputLayout_changePassword, dialog.text_changePassword, resources)){
                getSeguridad(dialog, finca, linea, true)
            }
        }
    }

    fun getSeguridad(dialog: Dialog?, finca: String, linea: Int, changePassword: Boolean){
        viewModel.getSeguridad("Configuracion").observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.w(TAG, "Obteniendo contraseña...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Contraseña obtenida ${result.data[0]}")
                    validarSeguridad(dialog, finca, linea, result.data[0].contrasenna, changePassword)
                }
                is Resource.Failure -> {
                    Log.w(TAG, "${MessageBuilder.sFailure}, no se pudo obtener la contraseña")
                }
            }
        })
    }

    private fun validarSeguridad(dialog: Dialog?, finca: String, linea: Int, contrasenna: String, changePassword: Boolean ) {
        if (dialog != null) {
            if (FieldValidators.isValidate(dialog.textInputLayout_password, dialog.text_password, resources, "* Contraseña inválida", contrasenna)){
                dialog.dismiss()
                user.finca = finca
                user.linea = linea
                BundleActivity.updateBundle(requireActivity(), user)
                if(dialog.edit_text_cantidad.text.toString() == "")
                    updUser(user,0, changePassword, dialog.text_changePassword.text.toString())
                else
                    updUser(user, dialog.edit_text_cantidad.text.toString().toInt(), changePassword, dialog.text_changePassword.text.toString())

            }
        }
    }

    fun updUser(user: User, valor: Int, changePassword: Boolean, newPassword: String){
        viewModel.putUser(user, changePassword, newPassword).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.w(TAG, "Actulizando usuario...")
                }
                is Resource.Success -> {
                    Log.w(TAG, "Usuario actualizado ${result.data}")
                    updRendimiento(user, valor, false)
                    if (!result.data) {
                        BundleActivity.updateBundle(requireActivity(), (requireActivity().intent.getStringExtra("Nube")!!.toInt()+1).toString())
                        //sumarNotify(requireActivity().intent.getStringExtra("Nube")!!.toInt())
                    }else{
                        BundleActivity.updateBundle(requireActivity(), (requireActivity().intent.getStringExtra("Nube")!!.toInt()-1).toString())
                        //sumarNotify(requireActivity().intent.getStringExtra("Nube")!!.toInt())
                    }
                }
                is Resource.Failure -> {
                    Log.w(TAG, "${MessageBuilder.sFailure}, no se pudo actualizar el usuario")
                }
            }
        })
    }

//    private fun sumarNotify(cant: Int) {
//        //requireActivity().notificationBadge.setNumber(cant)
//    }

    // FUNCIONES UTILIZADAS POR OTROS FRAGMENTOS

    fun getTallosPorHora(operarioLinea: OperarioLinea, rendimientoPorHora: TextView?, progressHorizontal: ProgressBar?) {
        viewModel.getRendimientoPorHora(operarioLinea, rendimiento).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar(true, progressHorizontal!!)
                }
                is Resource.Success -> {
                    showProgressBar(false, progressHorizontal!!)
                    rendimientoPorHora!!.text = "Tallos por hora: ${result.data}"
                }
                is Resource.Failure -> {
                    rendimientoPorHora!!.text = result.exception.message.toString()
                }
            }
        })
    }

    fun showProgressBar(show: Boolean, progress_horizontal: ProgressBar) {
        progress_horizontal.isVisible = show
    }
}