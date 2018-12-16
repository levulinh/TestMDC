package com.hellosg.studio.testmdc.configs

object Configs {
    const val key_pref = "key_pref"
    const val key_done_setup = "key_done_setup"
    const val mqtt_server_uri = "tcp://broker.mqttdashboard.com:1883"
    const val intent_key_type = "type"
    const val key_pref_device_id = "pref_device_id"
    const val intent_d_type_tv = 0
    const val intent_d_type_fan = 1
    const val intent_d_type_air_con = 2

    const val BASE_URL = "https://mandevices-hk-dev.herokuapp.com"
    const val ADD_DEVICE_URL = "$BASE_URL/devices/add"
    const val GET_DEVICES_URL = "$BASE_URL/devices/get"
    const val WIFI_SSID_CONFIG_URL = "http://192.168.4.1/config/ssid"
    const val WIFI_PASSWORD_CONFIG_URL = "http://192.168.4.1/config/password"
}