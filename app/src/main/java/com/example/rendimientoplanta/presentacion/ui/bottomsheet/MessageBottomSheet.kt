package com.example.rendimientoplanta.presentacion.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.rendimientoplanta.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_message.*
import kotlinx.android.synthetic.main.bottom_sheet_message.view.*


class MessageBottomSheet : BottomSheetDialogFragment() {
    private val args: MessageBottomSheetArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val  view = inflater.inflate(R.layout.bottom_sheet_message, container, false)
        view.advetenciaText.text = args.message
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aceptar.setOnClickListener { dismiss() }
    }
}