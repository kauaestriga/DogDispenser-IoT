package com.example.dogdispenser.api.model.triggerEvent

import com.google.gson.annotations.SerializedName

data class EventRequest(
    @SerializedName("deviceUUID")
    val deviceUUID: String,

    @SerializedName("action")
    val action: String
)
