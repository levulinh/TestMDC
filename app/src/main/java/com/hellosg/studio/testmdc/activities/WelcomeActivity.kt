package com.hellosg.studio.testmdc.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hellosg.studio.testmdc.R
import com.hellosg.studio.testmdc.configs.Configs
import kotlinx.android.synthetic.main.activity_welcome.*
import org.jetbrains.anko.intentFor

class WelcomeActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var doneFirstSetup = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(Configs.key_pref, Context.MODE_PRIVATE)
        doneFirstSetup = sharedPreferences.getBoolean(Configs.key_done_setup, false)

        if (doneFirstSetup) {
            startActivity(intentFor<MainActivity>())
            finish()
        }

        setContentView(R.layout.activity_welcome)

        setupEvents()
    }

    private fun setupEvents() {
        btn_get_started.setOnClickListener {
            startActivity(intentFor<LoginActivity>())
            finish()
        }
    }
}
