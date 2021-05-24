package com.example.dogdispenser.Application

import android.app.Application
import android.util.Log
import com.example.dogdispenser.R
import com.example.dogdispenser.api.model.insertBowlAmount.BowlRequest
import com.example.dogdispenser.api.model.insertDevice.DeviceRequest
import com.example.dogdispenser.api.service.APIService
import com.example.dogdispenser.utils.Dispenser
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_settings.*
import retrofit2.Call
import retrofit2.Response

class MyApplication: Application() {

    var TAG = "Device"

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this);

        APIService.instance
            ?.insertDevice(DeviceRequest(
                "QwVpeSMpXmiY7jvdW6x2",
                "Dispenser de ração para cachorros",
                "DX-71 v1"
            ))
            ?.enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "Registration device successfully!")
                    } else {
                        Log.d(TAG, "Error: ${response.errorBody().toString()}")
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d(TAG, "Error: ${t.message.toString()}")
                }
            })
    }
}