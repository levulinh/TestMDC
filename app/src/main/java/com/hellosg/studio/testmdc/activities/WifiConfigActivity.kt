package com.hellosg.studio.testmdc.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.hellosg.studio.testmdc.GlobalState
import com.hellosg.studio.testmdc.R
import com.hellosg.studio.testmdc.configs.Configs
import kotlinx.android.synthetic.main.activity_remote.*
import kotlinx.android.synthetic.main.activity_wifi_config.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.jetbrains.anko.intentFor

class WifiConfigActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var queue: RequestQueue
    private lateinit var client: MqttAndroidClient
    private var hubId = ""
    private lateinit var builder: AlertDialog.Builder
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_config)

        sharedPreferences = getSharedPreferences(Configs.key_pref, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        hubId = sharedPreferences.getString(Configs.key_pref_device_id, "nodevice")

        queue = Volley.newRequestQueue(this)


        builder = AlertDialog.Builder(this)
                .setTitle(R.string.config_wait)
                .setMessage(R.string.config_please_wait)
                .setCancelable(false)
                .setIcon(R.drawable.robot_smile)

        txt_tip.text = getString(R.string.config_tip, "HouseKeeper ${hubId}", "mandevices")
        btn_connect.setOnClickListener {
            editor.putBoolean("isConfigWifi", true).apply()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(layout_parent.getWindowToken(), 0)

            val ssid = edt_ssid.text
            val password = edt_password.text

            val sendSsidRequest = JsonObjectRequest("${Configs.WIFI_SSID_CONFIG_URL}/$ssid",
                    null,
                    {},
                    {})
            val sendPasswordRequest = JsonObjectRequest("${Configs.WIFI_PASSWORD_CONFIG_URL}/$password",
                    null,
                    {},
                    {})
            queue.add(sendSsidRequest)
            queue.add(sendPasswordRequest)

            builder.create().show()
        }

        btn_finish.setOnClickListener {
            startActivity(intentFor<MainActivity>())
            finish()
        }

        btn_back.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onResume() {
        super.onResume()
        val state = application as GlobalState
        client = state.client
        mqttSetup()
    }

    private fun mqttSetup() {
        try {
            if (!client.isConnected) {
                val conToken = client.connect()
                conToken.actionCallback = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d("MQTT", "Connected")
                        client.subscribe("mandevices/state/$hubId", 1)
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d("MQTT", "Connection failed")
                    }
                }
            } else client.subscribe("mandevices/state/$hubId", 1)

            client.setCallback(object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    Log.d("MQTT CHECK", topic)
                    if (topic == "mandevices/state/$hubId" &&
                            message?.toString() == "connected" &&
                            sharedPreferences.getBoolean("isConfigWifi", false))
                    {
                        editor.putBoolean("isConfigWifi", false).apply()
                        startActivity(intentFor<MainActivity>())

                    }
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.d("MQTT", "Connection lost ${if (client.isConnected) "YES" else "NO"}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d("MQTT", "Message sent")
                }
            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }
}
