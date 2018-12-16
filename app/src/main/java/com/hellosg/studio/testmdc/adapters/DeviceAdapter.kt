package com.hellosg.studio.testmdc.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hellosg.studio.testmdc.R
import com.hellosg.studio.testmdc.models.Device


class DeviceAdapter(var devices: ArrayList<Device> = arrayListOf(), var mListener: (Device) -> Unit)
    : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DeviceViewHolder {
        var view = LayoutInflater.from(p0.context).inflate(R.layout.item_device, p0, false)
        return DeviceViewHolder(view, mListener)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(p0: DeviceViewHolder, p1: Int) {
        val device = devices[p1]

        p0.txtDeviceName.text = device.name
        p0.txtDeviceName.setOnClickListener { p0.listener(device) }
    }

    fun updateData(devices: ArrayList<Device>) {
        this.devices.clear()
        this.devices.addAll(devices)
        notifyDataSetChanged()
    }

    inner class DeviceViewHolder(itemView: View, val listener: (Device) -> Unit) : RecyclerView.ViewHolder(itemView){

        var txtDeviceName: TextView = itemView.findViewById(R.id.txt_device_name)
    }
}