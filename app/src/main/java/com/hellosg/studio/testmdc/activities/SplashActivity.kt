package com.hellosg.studio.testmdc.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hellosg.studio.testmdc.GlobalState
import com.hellosg.studio.testmdc.R
import com.hellosg.studio.testmdc.configs.Configs
import kotlinx.android.synthetic.main.activity_splash.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.jetbrains.anko.intentFor

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var client: MqttAndroidClient
    private var clientId = ""
    private var isConnected = false
    private var overTime = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPreferences = getSharedPreferences(Configs.key_pref, Context.MODE_PRIVATE)

        val state = applicationContext as GlobalState
        client = state.client
        clientId = state.clientId
        setupMQTT()

        val handler = Handler()
        handler.postDelayed({
            if (isConnected) {
                if (!sharedPreferences.getBoolean(Configs.key_done_setup, false))
                    startActivity(intentFor<WelcomeActivity>())
                else startActivity(intentFor<MainActivity>())
                finish()
            } else {
                overTime = true
            }
        }, 1000)
        handler.postDelayed({
            txt_error.visibility = View.VISIBLE
        }, 3000)
    }

    private fun setupMQTT() {
        try {
            val mqttConnectOptions = MqttConnectOptions()
            mqttConnectOptions.apply {
                isCleanSession = false
                isAutomaticReconnect = true
                setWill("mandevices/will", "I'm going offline/$clientId".toByteArray(), 1, true)
            }

            val connectToken = client.connect(mqttConnectOptions)
            connectToken.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    isConnected = true
                    if (overTime) {
                        startActivity(intentFor<WelcomeActivity>())
                        finish()
                    }
                    Log.d("MQTT", "Connected")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    isConnected = false
                    Log.d("MQTT", "Not Connected")
                }
            }
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }
}
