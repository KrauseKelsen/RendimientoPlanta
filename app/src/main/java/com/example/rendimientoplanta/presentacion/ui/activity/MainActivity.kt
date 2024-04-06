package com.example.rendimientoplanta.presentacion.ui.activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.rendimientoplanta.R
import com.example.rendimientoplanta.base.utils.instances.BundleActivity
import com.example.rendimientoplanta.base.utils.instances.Network
import com.example.rendimientoplanta.presentacion.ui.dialogs.DialogMessage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*

/**
 * MainActivity redirecciona inicialmente al LoadFragment
 */
class MainActivity : AppCompatActivity(){
    private val TAG = "MainActivity"
    lateinit var dispositivo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar) // Se añade el toolbar
        bottom_navigation.isVisible = false // ocultar el navbar inferior
        constaintLayout_toolbar.isVisible = false // ocultar el nav bar superior
        BundleActivity.updateBundle(this) // iniciliaza en la bolsa del app las variables globales
    }

    override fun onStart() {
        super.onStart()
        // Añade las opciones al AppBar inferior
        AppBarConfiguration(setOf(
            R.id.productoFragment,
            R.id.procesosFragment,
            R.id.cierreFragment,
            R.id.reporteFragment
        ))
        bottom_navigation.setupWithNavController(findNavController(R.id.main_fragment)) // redirecciona al loadFragment
    }
}