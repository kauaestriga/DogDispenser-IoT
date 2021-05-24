package com.example.dogdispenser.api.model.insertDevice

import com.google.gson.annotations.SerializedName

data class DeviceRequest(
    @SerializedName("deviceUUID")
    val deviceUUID: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("model")
    val model: String
)