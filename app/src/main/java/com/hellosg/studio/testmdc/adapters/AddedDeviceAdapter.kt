package com.hellosg.studio.testmdc.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.hellosg.studio.testmdc.GlobalState
import com.hellosg.studio.testmdc.R
import com.hellosg.studio.testmdc.models.Device

class AddedDeviceAdapter(var devices: ArrayList<Device>, var context: Context, var mListener: (Device) -> Unit) : RecyclerView.Adapter<AddedDeviceAdapter.DeviceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device_main, parent, false)
        return DeviceViewHolder(view, mListener)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]

        holder.txt_device_name.text = device.name
        holder.img_device_icon.setImageResource(when (device.type) {
            0 -> R.drawable.small_tv
            1 -> R.drawable.small_fan
            2 -> R.drawable.small_air_con
            else -> R.drawable.small_tv
        })
        holder.layout_parent.setOnClickListener{
            holder.listener(device)
        }
//        holder.layout_parent.background = ContextCompat.getDrawable(context, when (device.type) {
//            0 -> R.drawable.bg_blue_purple_gradient
//            1 -> R.drawable.bg_red_orage_gradient
//            2 -> R.drawable.bg_purple_pink_gradient
//            else -> R.drawable.bg_blue_purple_gradient
//        })
    }

    fun updateData(devices: ArrayList<Device>) {
        this.devices.clear()
        this.devices.addAll(devices)
        notifyDataSetChanged()
    }

    inner class DeviceViewHolder(itemView: View, val listener: (Device) -> Unit) : RecyclerView.ViewHolder(itemView){
        var txt_device_name = itemView.findViewById<TextView>(R.id.txt_device_name)
        var img_device_icon = itemView.findViewById<ImageView>(R.id.img_device_icon)
        var layout_parent = itemView.findViewById<LinearLayout>(R.id.layout_parent)
    }
}