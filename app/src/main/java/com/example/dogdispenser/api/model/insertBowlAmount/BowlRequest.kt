package com.example.dogdispenser.api.model.insertBowlAmount

import com.google.gson.annotations.SerializedName

data class BowlRequest(
    @SerializedName("amount")
    val amount: String
)
