package com.example.dogdispenser.api.model.deviceLevel

import com.google.gson.annotations.SerializedName

data class DeviceLevelResponse(
    @SerializedName("kilos")
    val kilos: String,

    @SerializedName("percentageValue")
    val percentageValue: String
)