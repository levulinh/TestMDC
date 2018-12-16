package com.hellosg.studio.testmdc.models

import java.io.Serializable

data class Device(var name: String, var type: Int, var buttons: HashMap<String, String>?) : Serializable