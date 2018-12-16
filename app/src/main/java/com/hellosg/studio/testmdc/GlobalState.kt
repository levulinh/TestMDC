package com.hellosg.studio.testmdc

import android.app.Application
import com.hellosg.studio.testmdc.configs.Configs
import com.hellosg.studio.testmdc.models.Device
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttClient

class GlobalState : Application() {

    var clientId = ""
    lateinit var client: MqttAndroidClient
    var currentDevice: Device? = null
    var currentDevicesList = arrayListOf<Device>()

    override fun onCreate() {
        super.onCreate()

        clientId = MqttClient.generateClientId()
        client = MqttAndroidClient(applicationContext, Configs.mqtt_server_uri, clientId)
    }
}