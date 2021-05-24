package com.example.dogdispenser.api.model.rechargeDevice

import com.google.gson.annotations.SerializedName

data class RechargeRequest(

    @SerializedName("kilos")
    val kilos: String
)
