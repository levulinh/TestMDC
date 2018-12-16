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

class FanRemoteFragment: Fragment(), View.OnClickListener {
    override fun onClick(v: View?) {
        activityCallback?.onClick(v!!.tag.toString())
    }

    private var activityCallback: OnFanButtonClick? = null

    interface OnFanButtonClick{
        fun onClick(tag: String)
    }

    //2
    companion object {
        fun newInstance(): FanRemoteFragment {
            return FanRemoteFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val childView = inflater.inflate(R.layout.fragment_fan, container, false)

        ///////////////// DEFINE EVERY SINGLE BUTTON
        val btn_power = childView.findViewById<FloatingActionButton>(R.id.btn_power)
        val btn_low = childView.findViewById<Button>(R.id.btn_low)
        val btn_med = childView.findViewById<Button>(R.id.btn_med)
        val btn_hi = childView.findViewById<Button>(R.id.btn_hi)
        val btn_swing_horz = childView.findViewById<Button>(R.id.btn_swing_horz)
        val btn_sleep = childView.findViewById<Button>(R.id.btn_sleep)

        ////////////// SET THEM ON CLICK LISTENER
        btn_power.setOnClickListener(this)
        btn_low.setOnClickListener(this)
        btn_med.setOnClickListener(this)
        btn_hi.setOnClickListener(this)
        btn_swing_horz.setOnClickListener(this)
        btn_sleep.setOnClickListener(this)

        return childView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            activityCallback = context as OnFanButtonClick
        } catch (e: ClassCastException) {
            throw ClassCastException(context?.toString()
                    + " must implement ToolbarListener")
        }
    }
}