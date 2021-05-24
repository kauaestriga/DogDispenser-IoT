package com.example.dogdispenser.api.service

import com.example.dogdispenser.api.model.deviceLevel.DeviceLevelResponse
import com.example.dogdispenser.api.model.getBowlAmount.BowlResponse
import com.example.dogdispenser.api.model.insertBowlAmount.BowlRequest
import com.example.dogdispenser.api.model.insertDevice.DeviceRequest
import com.example.dogdispenser.api.model.rechargeDevice.RechargeRequest
import com.example.dogdispenser.api.model.triggerEvent.EventRequest
import retrofit2.Call
import retrofit2.http.*

interface DogDispenserService {

    @POST("/device")
    fun insertDevice(@Body deviceRequest: DeviceRequest) : Call<Unit>

    @GET("/device/{id}")
    fun getDevice(@Path("id") id: String) : Call<DeviceRequest>

    @POST("/device/{id}/recharge")
    fun rechargeDevice(@Path("id") id: String, @Body rechargeRequest: RechargeRequest) : Call<Unit>

    @POST("/device/event")
    fun triggerAction(@Body eventRequest: EventRequest) : Call<Unit>

    @GET("/device/{id}/level")
    fun getDeviceLevel(@Path("id") id: String) : Call<DeviceLevelResponse>

    @GET("/schedules/{id}")
    fun getSchedulesTrigger(@Path("id") id: String) : Call<HashMap<String, Boolean>>

    @FormUrlEncoded
    @POST("/schedules/{id}")
    fun insertScheduleTrigger(@Path("id") id: String, @FieldMap scheduleMap: Map<String, Boolean>) : Call<Unit>

    @POST("/bowl/{id}")
    fun insertBowlAmount(@Path("id") id: String, @Body amount: BowlRequest) : Call<Unit>

    @GET("/bowl/{id}")
    fun getBowlAmount(@Path("id") id: String) : Call<BowlResponse>
}