package com.example.dogdispenser.api.model.getBowlAmount

import com.google.gson.annotations.SerializedName

data class BowlResponse(
    @SerializedName("amount")
    val amount: String
)
