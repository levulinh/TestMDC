package com.hellosg.studio.testmdc.fragments

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hellosg.studio.testmdc.GlideApp
import com.hellosg.studio.testmdc.R

class UserBottomSheet: BottomSheetDialogFragment() {

    companion object {
        fun newInstance(name: String?, email: String?, photoUri: Uri?): UserBottomSheet {
            val args = Bundle()
            args.putString("name", name)
            args.putString("email", email)
            args.putString("photoUri", photoUri.toString())
            val fragment = UserBottomSheet()
            fragment.arguments = args
            return fragment
        }
    }

    private var activityCallback: OnClickListener? = null

    interface OnClickListener {
        fun onLogout()
    }

//    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_user, container, false)

        val btn_settings = view.findViewById<LinearLayout>(R.id.action_settings)
        val btn_help = view.findViewById<LinearLayout>(R.id.action_help)
        val btn_logout = view.findViewById<LinearLayout>(R.id.action_logout)

        val img_user_avatar = view.findViewById<ImageView>(R.id.img_avatar_bottom)
        val txt_user_name = view.findViewById<TextView>(R.id.txt_user_name)
        val txt_user_email = view.findViewById<TextView>(R.id.txt_user_email)

        txt_user_name.text = arguments?.getString("name")
        txt_user_email.text = arguments?.getString("email")
        GlideApp.with(context!!)
                .load(Uri.parse(arguments?.getString("photoUri")))
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.user)
                .into(img_user_avatar)

        btn_logout.setOnClickListener {
            activityCallback?.onLogout()
        }

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            activityCallback = context as OnClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context?.toString()
                    + " must implement OnClickListener")
        }
    }
}