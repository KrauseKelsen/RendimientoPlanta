package com.example.rendimientoplanta.presentacion.ui.fragment

import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.fragment_login.progress_horizontal

abstract class ILoginFragment(bn_visible: Boolean, cl_visible: Boolean, subtitle: String) : Fragment(){
    private val TAG = "ILoginFragment"

    private var pbn_visible = bn_visible
    private var pcl_visible = cl_visible
    private var psubtitle = subtitle
    lateinit var dispositivo: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dispositivo = Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
        requireActivity().toolbar.subtitle = psubtitle
        requireActivity().bottom_navigation.isVisible = pbn_visible
        requireActivity().constaintLayout_toolbar.isVisible = pcl_visible
        requireActivity().btnConfig.isVisible = false
        return inflater.inflate(getViewID(), container, false)
    }

    private fun sumarNotify(){
        requireActivity().notificationBadge.setNumber(99)
    }

    protected abstract fun getViewID():Int

}