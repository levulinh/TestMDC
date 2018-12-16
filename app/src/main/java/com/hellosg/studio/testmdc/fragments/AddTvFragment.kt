package com.hellosg.studio.testmdc.fragments

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hellosg.studio.testmdc.R

class AddTvFragment : Fragment(), View.OnClickListener {

    override fun onClick(v: View?) {
        val states = arrayOf(intArrayOf(android.R.attr.state_enabled))

        val colors = intArrayOf(Color.parseColor("#23af0e"))

        val myList = ColorStateList(states, colors)

        v?.backgroundTintList = myList
        activityCallback?.onClickTv(v!!)
    }

    private var activityCallback: OnLearningButtonClick? = null
    lateinit var childView: View

    interface OnLearningButtonClick {
        fun onClickTv(v: View)
    }

    companion object {
        fun newInstance(): AddTvFragment {
            return AddTvFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        childView = inflater.inflate(R.layout.fragment_tv, container, false)

        ///////////////// DEFINE EVERY SINGLE BUTTON
        val btn_power = childView.findViewById<FloatingActionButton>(R.id.btn_power)
        val btn_volume_mute = childView.findViewById<FloatingActionButton>(R.id.btn_volume_mute)
        val btn_volume_up = childView.findViewById<FloatingActionButton>(R.id.btn_volume_up)
        val btn_volume_down = childView.findViewById<FloatingActionButton>(R.id.btn_volume_down)
        val btn_channel_0 = childView.findViewById<FloatingActionButton>(R.id.btn_channel_0)
        val btn_channel_1 = childView.findViewById<FloatingActionButton>(R.id.btn_channel_1)
        val btn_channel_2 = childView.findViewById<FloatingActionButton>(R.id.btn_channel_2)
        val btn_channel_3 = childView.findViewById<FloatingActionButton>(R.id.btn_channel_3)
        val btn_channel_4 = childView.findViewById<FloatingActionButton>(R.id.btn_channel_4)
        val btn_channel_5 = childView.findViewById<FloatingActionButton>(R.id.btn_channel_5)
        val btn_channel_6 = childView.findViewById<FloatingActionButton>(R.id.btn_channel_6)
        val btn_channel_7 = childView.findViewById<FloatingActionButton>(R.id.btn_channel_7)
        val btn_channel_8 = childView.findViewById<FloatingActionButton>(R.id.btn_channel_8)
        val btn_channel_9 = childView.findViewById<FloatingActionButton>(R.id.btn_channel_9)
        val btn_ok = childView.findViewById<FloatingActionButton>(R.id.btn_ok)
        val btn_left = childView.findViewById<FloatingActionButton>(R.id.btn_left)
        val btn_right = childView.findViewById<FloatingActionButton>(R.id.btn_right)
        val btn_up = childView.findViewById<FloatingActionButton>(R.id.btn_up)
        val btn_down = childView.findViewById<FloatingActionButton>(R.id.btn_down)
        val btn_channel_down = childView.findViewById<FloatingActionButton>(R.id.btn_channel_down)
        val btn_channel_up = childView.findViewById<FloatingActionButton>(R.id.btn_temp_up)
        val btn_menu = childView.findViewById<FloatingActionButton>(R.id.btn_menu)

        ////////////// SET THEM ON CLICK LISTENER
        btn_power.setOnClickListener(this)
        btn_volume_mute.setOnClickListener(this)
        btn_volume_up.setOnClickListener(this)
        btn_volume_down.setOnClickListener(this)
        btn_channel_0.setOnClickListener(this)
        btn_channel_1.setOnClickListener(this)
        btn_channel_2.setOnClickListener(this)
        btn_channel_3.setOnClickListener(this)
        btn_channel_4.setOnClickListener(this)
        btn_channel_5.setOnClickListener(this)
        btn_channel_6.setOnClickListener(this)
        btn_channel_7.setOnClickListener(this)
        btn_channel_8.setOnClickListener(this)
        btn_channel_9.setOnClickListener(this)
        btn_ok.setOnClickListener(this)
        btn_left.setOnClickListener(this)
        btn_right.setOnClickListener(this)
        btn_up.setOnClickListener(this)
        btn_down.setOnClickListener(this)
        btn_channel_down.setOnClickListener(this)
        btn_channel_up.setOnClickListener(this)
        btn_menu.setOnClickListener(this)

        return childView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            activityCallback = context as OnLearningButtonClick
        } catch (e: ClassCastException) {
            throw ClassCastException(context?.toString()
                    + " must implement ToolbarListener")
        }
    }
}