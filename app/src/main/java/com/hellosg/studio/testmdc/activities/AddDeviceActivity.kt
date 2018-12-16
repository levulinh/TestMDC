package com.hellosg.studio.testmdc.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.hellosg.studio.testmdc.GlobalState
import com.hellosg.studio.testmdc.R
import com.hellosg.studio.testmdc.configs.Configs
import com.hellosg.studio.testmdc.fragments.*
import kotlinx.android.synthetic.main.activity_add_device.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.jetbrains.anko.intentFor
import org.json.JSONArray
import org.json.JSONObject
import java.io.UnsupportedEncodingException


class AddDeviceActivity : AppCompatActivity(), AddTvFragment.OnLearningButtonClick
, AddFanFragment.OnLearningButtonClick, AddAirConFragment.OnLearningButtonClick{

    private var type = 0
    private var buttonNumberList = listOf(2, 2, 2)
//    private var buttonNumberList = listOf(22, 6, 9)
    private var deviceName = "If you see this line please report a bug"
    private var currentFragment: Fragment = AddTvFragment.newInstance()
    private var isWaiting = false
    private var waitingTag = ""
    private lateinit var sharedPreferences: SharedPreferences
    private var hubId = ""
    private lateinit var alertDialog: AlertDialog
    private lateinit var client: MqttAndroidClient
    private var doneList = mutableListOf<String>()
    private var doneMap = HashMap<String, String>()
    private var buttonArray = JSONArray()

    private lateinit var queue: RequestQueue
    private lateinit var requestObject: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)

        sharedPreferences = getSharedPreferences(Configs.key_pref, Context.MODE_PRIVATE)
        hubId = sharedPreferences.getString(Configs.key_pref_device_id, "")!!

        alertDialog = AlertDialog.Builder(this)
                .setTitle(R.string.add_please_wait)
                .setMessage(R.string.add_wait_for_response)
                .setCancelable(false)
                .setPositiveButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .create()

        type = intent.getIntExtra(Configs.intent_key_type, Configs.intent_d_type_tv)

        deviceName = when (type) {
            Configs.intent_d_type_tv -> getString(R.string.add_new_tv)
            Configs.intent_d_type_fan -> getString(R.string.add_new_fan)
            Configs.intent_d_type_air_con -> getString(R.string.add_new_air_con)
            else -> getString(R.string.add_new_tv)
        }

        Log.d("DEVICE NAME", deviceName)

        queue = Volley.newRequestQueue(this)
        requestObject = JSONObject()

        setFragment(type, savedInstanceState)

        btn_done.setOnClickListener {
            if (edt_device_name.text!!.isNotEmpty()) {
                til_device_name.error = null
                requestObject.put("name", edt_device_name.text)
                        .put("type", type)
                        .put("hubId", hubId)
                for ((key, value) in doneMap) {
                    buttonArray.put(JSONObject().put("tag", key).put("code", value))
                }
                requestObject.put("buttons", buttonArray)

                val addDeviceRequest = JsonObjectRequest(Request.Method.POST, Configs.ADD_DEVICE_URL, requestObject,
                        Response.Listener { response ->
                            alertDialog.dismiss()
                            startActivity(intentFor<MainActivity>())
                            Toast.makeText(this@AddDeviceActivity, R.string.add_success, Toast.LENGTH_SHORT).show()
                            finish()
                        },
                        Response.ErrorListener { error ->
                            alertDialog.dismiss()
                            Toast.makeText(this@AddDeviceActivity, R.string.add_error, Toast.LENGTH_SHORT).show()
                        }
                )
                alertDialog.show()
                queue.add(addDeviceRequest)
            } else {
                til_device_name.error = getString(R.string.error_no_empty)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val state = applicationContext as GlobalState
        client = state.client
        mqttSetup()
    }

    override fun onClickTv(v: View) {
        val topic = "mandevices/request/$hubId"
        val payload = client.clientId
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

        alertDialog.show()
        isWaiting = true
        waitingTag = v.tag.toString()
    }

    private fun setFragment(type: Int, savedInstanceState: Bundle?) {

        val fragmentToShow: Fragment = when (type) {
            Configs.intent_d_type_tv -> AddTvFragment.newInstance()
            Configs.intent_d_type_fan -> AddFanFragment.newInstance()
            Configs.intent_d_type_air_con -> AddAirConFragment.newInstance()
            else -> AddTvFragment.newInstance()
        }

        currentFragment = fragmentToShow

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragmentToShow)
                    .commit()
        }
    }

    private fun mqttSetup() {
        try {
            if (!client.isConnected) {
                val conToken = client.connect()
                conToken.actionCallback = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d("MQTT", "Connected")
                        testSubscribe()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d("MQTT", "Connection failed")
                    }
                }
            } else testSubscribe()

            client.setCallback(object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    if (isWaiting && alertDialog.isShowing) {
                        isWaiting = false
                        alertDialog.dismiss()
                        if (!doneList.contains(waitingTag)) doneList.add(waitingTag)
                        doneMap.put(waitingTag, message.toString())
                        if (doneList.size == buttonNumberList[type])
                            btn_done.isEnabled = true
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

    private fun testSubscribe() {
        val subToken = client.subscribe("mandevices/response/$hubId/${client.clientId}", 1)
        subToken.actionCallback = object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d("MQTT", "Success")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d("MQTT", "Failed")
            }
        }
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
