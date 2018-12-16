package com.hellosg.studio.testmdc.fragments

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hellosg.studio.testmdc.GlobalState
import com.hellosg.studio.testmdc.R
import com.hellosg.studio.testmdc.adapters.DeviceAdapter
import com.hellosg.studio.testmdc.models.Device
import kotlinx.android.synthetic.main.bottom_sheet_devices.*


class ListDeviceBottomSheet : BottomSheetDialogFragment() {

    private var activityCallback: OnClickListener? = null

    interface OnClickListener {
        fun onItemSelected(device: Device)
    }

//    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    companion object {
        fun newInstance(devices: ArrayList<Device>): ListDeviceBottomSheet {
            val listFragment = ListDeviceBottomSheet()
            val bundle = Bundle()
            bundle.putSerializable("DEVICES", devices)
            listFragment.arguments = bundle
            return listFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_devices, container, false)

        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val bottomSheet = d.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            val coordinatorLayout = bottomSheet?.parent as CoordinatorLayout
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.peekHeight = 650
            bottomSheetBehavior.isHideable = true
            coordinatorLayout.parent.requestLayout()
        }

        val rvDevices = view.findViewById<RecyclerView>(R.id.rv_devices)
        val devices: ArrayList<Device> = arguments?.getSerializable("DEVICES") as ArrayList<Device>

        val adapter = DeviceAdapter(devices) { device ->
            activityCallback?.onItemSelected(device)
        }

        rvDevices.setHasFixedSize(false)
        rvDevices.adapter = adapter
        rvDevices.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rvDevices.setOnScrollChangeListener { _, _, _, _, _ ->
                header.isSelected = rvDevices.canScrollVertically(-1)
            }
        }

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            activityCallback = context as OnClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context?.toString()
                    + " must implement ToolbarListener")
        }
    }
}