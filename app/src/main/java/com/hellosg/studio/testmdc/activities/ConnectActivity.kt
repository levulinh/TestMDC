package com.hellosg.studio.testmdc.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.hellosg.studio.testmdc.R
import com.hellosg.studio.testmdc.configs.Configs
import kotlinx.android.synthetic.main.activity_connect.*
import org.jetbrains.anko.intentFor

class ConnectActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)

        sharedPreferences = getSharedPreferences(Configs.key_pref, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        setEvents()
    }

    private fun setEvents() {
        btn_back.setOnClickListener {
            finish()
        }

        val hubId = getSharedPreferences(Configs.key_pref, Context.MODE_PRIVATE)
                .getString(Configs.key_pref_device_id, "no_device")

        txt_device_id.setText(hubId)

        txt_device_id.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                // do nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count < 5) {
                    til_device_id.error = getString(R.string.connect_id_error)
                    btn_connect.isEnabled = false
                } else {
                    til_device_id.error = null
                    btn_connect.isEnabled = true
                }
            }
        })

        btn_connect.setOnClickListener {
            editor.putString(Configs.key_pref_device_id, txt_device_id.text.toString())
            btn_finish.isEnabled = true
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(layout_parent.getWindowToken(), 0)
            editor.putBoolean(Configs.key_done_setup, true)
            editor.apply()
            if (txt_device_id.text!!.length < 5)
                til_device_id.error = getString(R.string.connect_id_error)
            else til_device_id.error = null

            startActivity(intentFor<WifiConfigActivity>())
        }

        btn_finish.setOnClickListener {
            editor.putBoolean(Configs.key_done_setup, true)
            editor.apply()
            startActivity(intentFor<WifiConfigActivity>())
            finish()
        }
    }
}
