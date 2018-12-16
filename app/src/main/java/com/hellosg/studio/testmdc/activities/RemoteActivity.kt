package com.hellosg.studio.testmdc.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.hellosg.studio.testmdc.GlobalState
import com.hellosg.studio.testmdc.R
import com.hellosg.studio.testmdc.configs.Configs
import com.hellosg.studio.testmdc.fragments.*
import com.hellosg.studio.testmdc.models.Device
import kotlinx.android.synthetic.main.activity_remote.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.io.UnsupportedEncodingException

class RemoteActivity : AppCompatActivity(), ListDeviceBottomSheet.OnClickListener, TvRemoteFragment.OnTvButtonClick, FanRemoteFragment.OnFanButtonClick, AirConRemoteFragment.OnAirConButtonClick{

    private var type = 0
    private var deviceName = "If you see this line please report a bug"
    private var hubId = ""
    private var bottomSheet = ListDeviceBottomSheet()
    private lateinit var client: MqttAndroidClient
    private var device: Device? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote)

        val state = applicationContext as GlobalState
        device = state.currentDevice
        hubId = getSharedPreferences(Configs.key_pref, Context.MODE_PRIVATE)
                .getString(Configs.key_pref_device_id, "no_device")

        setFragment(device, savedInstanceState)

        top_toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp)
        top_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        txt_show_devices.setOnClickListener {
            bottomSheet = ListDeviceBottomSheet.newInstance((applicationContext as GlobalState).currentDevicesList)
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
    }

    override fun onResume() {
        super.onResume()

        val state = applicationContext as GlobalState
        client = state.client
        mqttSetup()
    }

    override fun onClick(tag: String) {
        mqttPublish("mandevices/control/$hubId", device!!.buttons!!.get(tag)!!)
    }

    private fun mqttPublish(topic: String, payload: String){
        val encodedPayload: ByteArray
        try {
            encodedPayload = payload.toByteArray(charset("UTF-8"))
            val message = MqttMessage(encodedPayload)
            client.publish(topic, message)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private fun mqttSetup() {
        try {
            if (!client.isConnected) {
                layout_connecting.visibility = View.VISIBLE
                fragment_container.visibility = View.GONE
                txt_show_devices.visibility = View.GONE

                val conToken = client.connect()
                conToken.actionCallback = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d("MQTT", "Connected")
                        layout_connecting.visibility = View.GONE
                        fragment_container.visibility = View.VISIBLE
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d("MQTT", "Connection failed")
                    }
                }
            }

            client.setCallback(object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    Log.d("MQTT", "$topic - ${message.toString()}")
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

    private fun setFragment(device: Device?, savedInstanceState: Bundle?) {
        if (device == null) {
            type = intent.getIntExtra("type", 0)
            deviceName = intent.getStringExtra("name")
        } else {
            type = device.type
            deviceName = device.name
        }

        val fragmentToShow: Fragment = when (type) {
            0 -> TvRemoteFragment.newInstance()
            1 -> FanRemoteFragment.newInstance()
            2 -> AirConRemoteFragment.newInstance() // 2
            else -> TvRemoteFragment.newInstance()
        }

        top_toolbar.title = deviceName

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragmentToShow)
                    .commit()
        }
    }

    override fun onItemSelected(device: Device) {
        bottomSheet.dismiss()
        setFragment(device, null)
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}
