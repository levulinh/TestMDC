package com.hellosg.studio.testmdc.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hellosg.studio.testmdc.R
import com.hellosg.studio.testmdc.activities.AddDeviceActivity
import com.hellosg.studio.testmdc.configs.Configs

class AddDeviceBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {

    override fun onClick(v: View?) {
        val intent = Intent(activity, AddDeviceActivity::class.java)

        intent.putExtra(Configs.intent_key_type, when (v?.id) {
            R.id.btn_add_tv -> Configs.intent_d_type_tv
            R.id.btn_add_fan -> Configs.intent_d_type_fan
            R.id.btn_add_air_con -> Configs.intent_d_type_air_con
            else -> Configs.intent_d_type_tv
        })
        startActivity(intent)
        dialog.dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_add_device, container, false)
        val btn_add_tv = view.findViewById<Button>(R.id.btn_add_tv)
        btn_add_tv.setOnClickListener(this)
        val btn_add_fan = view.findViewById<Button>(R.id.btn_add_fan)
        btn_add_fan.setOnClickListener(this)
        val btn_add_air_con = view.findViewById<Button>(R.id.btn_add_air_con)
        btn_add_air_con.setOnClickListener(this)
        return view
    }
}