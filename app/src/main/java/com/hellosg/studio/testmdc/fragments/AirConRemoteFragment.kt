package com.hellosg.studio.testmdc.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hellosg.studio.testmdc.R

class AirConRemoteFragment: Fragment(), View.OnClickListener {

    override fun onClick(v: View?) {
        activityCallback?.onClick(v!!.tag.toString())
    }

    private var activityCallback: OnAirConButtonClick? = null

    interface OnAirConButtonClick{
        fun onClick(tag: String)
    }

    //2
    companion object {
        fun newInstance(): AirConRemoteFragment {
            return AirConRemoteFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val childView = inflater.inflate(R.layout.fragment_aircon, container, false)

        ///////////////// DEFINE EVERY SINGLE BUTTON
        val btn_power = childView.findViewById<FloatingActionButton>(R.id.btn_power)
        val btn_fan = childView.findViewById<Button>(R.id.btn_fan)
        val btn_mode = childView.findViewById<Button>(R.id.btn_mode)
        val btn_temp_up = childView.findViewById<FloatingActionButton>(R.id.btn_temp_up)
        val btn_temp_down = childView.findViewById<FloatingActionButton>(R.id.btn_temp_down)
        val btn_sleep = childView.findViewById<Button>(R.id.btn_sleep)
        val btn_swing_horz = childView.findViewById<Button>(R.id.btn_swing_horz)
        val btn_swing_vert = childView.findViewById<Button>(R.id.btn_swing_vert)
        val btn_turbo = childView.findViewById<Button>(R.id.btn_turbo)

        ////////////// SET THEM ON CLICK LISTENER
        btn_power.setOnClickListener(this)
        btn_fan.setOnClickListener(this)
        btn_mode.setOnClickListener(this)
        btn_temp_down.setOnClickListener(this)
        btn_temp_up.setOnClickListener(this)
        btn_swing_vert.setOnClickListener(this)
        btn_swing_horz.setOnClickListener(this)
        btn_sleep.setOnClickListener(this)
        btn_turbo.setOnClickListener(this)

        return childView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            activityCallback = context as OnAirConButtonClick
        } catch (e: ClassCastException) {
            throw ClassCastException(context?.toString()
                    + " must implement ToolbarListener")
        }
    }
}