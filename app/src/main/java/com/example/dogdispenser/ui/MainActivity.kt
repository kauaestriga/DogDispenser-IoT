package com.example.dogdispenser.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.dogdispenser.R
import com.example.dogdispenser.api.model.deviceLevel.DeviceLevelResponse
import com.example.dogdispenser.api.model.getBowlAmount.BowlResponse
import com.example.dogdispenser.api.model.rechargeDevice.RechargeRequest
import com.example.dogdispenser.api.model.triggerEvent.EventRequest
import com.example.dogdispenser.api.service.APIService
import com.example.dogdispenser.utils.Dispenser
import com.example.dogdispenser.utils.GeneralUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_settings.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {

    var isBowlFull = false
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        configView()
        loadEnableTrigger()
        loadData()
        verifyBowl()
    }

    override fun onRestart() {
        super.onRestart()
        loadEnableTrigger()
    }

    private fun configView() {
        tv_qtd_dispenser.text = getString(R.string.qtd_dispenser, "0")
        tv_qtd_bowl.text = getString(R.string.qtd_bowl, "vazio")

        btn_recharge.setOnClickListener {
            rechargeDevice()
        }

        btn_trigger.setOnClickListener {
            activeTrigger()
        }
    }

    private fun loadData() {
        APIService.instance
            ?.getDeviceLevel(Dispenser.id)
            ?.enqueue(object : Callback<DeviceLevelResponse> {
                override fun onResponse(
                    call: Call<DeviceLevelResponse>,
                    response: Response<DeviceLevelResponse>
                ) {
                    if (response.isSuccessful) {
                        val df = DecimalFormat("0.000")
                        var kilos = df.format(response.body()?.kilos?.toFloat())

                        tv_qtd_dispenser.text = getString(
                            R.string.qtd_dispenser,
                                kilos
                            )
                    } else {
                        tv_qtd_dispenser.text = getString(R.string.qtd_dispenser,
                            "0"
                        )
                    }
                }

                override fun onFailure(call: Call<DeviceLevelResponse>, t: Throwable) {
                    Log.d(TAG, t.message.toString())
                }
            })
    }

    private fun verifyBowl() {
        APIService.instance
            ?.getSchedulesTrigger(Dispenser.id)
            ?.enqueue(object : Callback<HashMap<String, Boolean>> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<HashMap<String, Boolean>>,
                    response: Response<HashMap<String, Boolean>>
                ) {
                    if (response.isSuccessful) {

                        response.body()?.forEach {doc ->
                            if (!isBowlFull) {
                                isBowlFull = isBowlFull(doc.key)
                            }
                        }

                        setValueBowl()
                    } else {
                        GeneralUtils.toast(baseContext, response.errorBody().toString())
                    }
                }

                override fun onFailure(call: Call<HashMap<String, Boolean>>, t: Throwable) {
                    Log.d(TAG, t.message.toString())
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setValueBowl() {
        if (isBowlFull)
            tv_qtd_bowl.text = getString(R.string.qtd_bowl, "cheio")
        else
            tv_qtd_bowl.text = getString(R.string.qtd_bowl, "vazio")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isBowlFull(scheduleTime: String) : Boolean{

        val currentTime = SimpleDateFormat("HH:mm").parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
        val minimumTime = SimpleDateFormat("HH:mm").parse(scheduleTime)
        var calendar = Calendar.getInstance()
        calendar.time = minimumTime
        calendar.add(Calendar.HOUR, 2)
        val maximumTime = calendar.time

        return (currentTime.after(minimumTime) && currentTime.before(maximumTime))
    }

    private fun rechargeDevice() {
        APIService.instance
            ?.rechargeDevice(Dispenser.id, RechargeRequest("10"))
            ?.enqueue(object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    if (response.isSuccessful) {
                        GeneralUtils.toast(baseContext, getString(R.string.success_recharge), false)
                        loadData()
                    } else {
                        GeneralUtils.toast(baseContext, response.errorBody().toString())
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    GeneralUtils.toast(baseContext, t.message.toString())
                }
            })
    }

    private fun activeTrigger() {
        APIService.instance
            ?.triggerAction(EventRequest(Dispenser.id, "TRIGGER"))
            ?.enqueue(object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    if (response.isSuccessful) {
                        GeneralUtils.toast(baseContext, getString(R.string.success_trigger), false)
                        isBowlFull = true
                        setValueBowl()
                        loadData()
                    } else {
                        GeneralUtils.toast(baseContext, response.errorBody().toString())
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d(TAG, t.message.toString())
                }
            })
    }

    private fun loadEnableTrigger() {
        APIService.instance
            ?.getBowlAmount(Dispenser.id)
            ?.enqueue(object : Callback<BowlResponse> {
                override fun onResponse(
                    call: Call<BowlResponse>,
                    response: Response<BowlResponse>
                ) {
                    btn_trigger.isEnabled = response.isSuccessful
                }

                override fun onFailure(call: Call<BowlResponse>, t: Throwable) {
                    Log.d(TAG, t.message.toString())
                    btn_trigger.isEnabled = false
                }
            })
    }
}