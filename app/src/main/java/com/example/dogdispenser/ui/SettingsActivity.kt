package com.example.dogdispenser.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dogdispenser.R
import com.example.dogdispenser.api.model.getBowlAmount.BowlResponse
import com.example.dogdispenser.api.model.insertBowlAmount.BowlRequest
import com.example.dogdispenser.api.service.APIService
import com.example.dogdispenser.utils.Constants
import com.example.dogdispenser.utils.Dispenser
import com.example.dogdispenser.utils.GeneralUtils
import com.example.dogdispenser.utils.MyClickListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.toolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class SettingsActivity : AppCompatActivity(), MyClickListener {

    private val db = FirebaseFirestore.getInstance()
    private val TAG = "SettingsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)

        viewConfigs()
        loadData()
        loadAmount()
    }

    fun viewConfigs() {
        btn_update.setOnClickListener {
            setBowlValue()
        }
    }

    private fun loadData() {
        APIService.instance
            ?.getSchedulesTrigger(Dispenser.id)
            ?.enqueue(object : Callback<HashMap<String, Boolean>> {
                override fun onResponse(
                    call: Call<HashMap<String, Boolean>>,
                    response: Response<HashMap<String, Boolean>>
                ) {
                    if (response.isSuccessful) {
                        val scheduleList: ArrayList<String> = ArrayList()
                        val enableList: ArrayList<Boolean> = ArrayList()

                        response.body()?.forEach {doc ->
                            scheduleList.add(doc.key)
                            enableList.add(doc.value)
                        }

                        populateList(scheduleList, enableList)
                    } else {
                        GeneralUtils.toast(baseContext, response.errorBody().toString())
                    }
                }

                override fun onFailure(call: Call<HashMap<String, Boolean>>, t: Throwable) {
                    Log.d(TAG, t.message.toString())
                }
            })
    }

    private fun populateList(scheduleList: ArrayList<String>, enableList: ArrayList<Boolean>) {
        val adapter = SettingsAdapter(scheduleList, enableList, this)
        rv_settings.adapter = adapter
    }

    private fun loadAmount() {
        APIService.instance
            ?.getBowlAmount(Dispenser.id)
            ?.enqueue(object : Callback<BowlResponse> {
                override fun onResponse(
                    call: Call<BowlResponse>,
                    response: Response<BowlResponse>
                ) {
                    if (response.isSuccessful) {
                        edt_amount_bowl.setText(response.body()?.amount)
                    } else {
                        GeneralUtils.toast(baseContext, response.errorBody().toString())
                    }
                }

                override fun onFailure(call: Call<BowlResponse>, t: Throwable) {
                    Log.d(TAG, t.message.toString())
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_add -> {
                setTime()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setTime() {
        val cal = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            var dateSelected = SimpleDateFormat("HH:mm").format(cal.time)

            APIService.instance
                ?.insertScheduleTrigger(Dispenser.id, mapOf(dateSelected to true))
                ?.enqueue(object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>
                    ) {
                        if (response.isSuccessful) {
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

        TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }

    override fun onLongClick(schedule: String) {
        val docRef = db.collection(Constants.SCHEDULES).document(Dispenser.id)

        val updates = hashMapOf<String, Any>(
            schedule to FieldValue.delete()
        )

        docRef.update(updates).addOnCompleteListener {
            Toast.makeText(this, "Excluído com sucesso!", Toast.LENGTH_SHORT).show()
            loadData()
        }
    }

    private fun setBowlValue() {
        APIService.instance
            ?.insertBowlAmount(Dispenser.id, BowlRequest(edt_amount_bowl.text.toString()))
            ?.enqueue(object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    if (response.isSuccessful) {
                        GeneralUtils.toast(baseContext, getString(R.string.success_save), false)
                        GeneralUtils.hideKeyboard(baseContext)
                    } else {
                        GeneralUtils.toast(baseContext, response.errorBody().toString())
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d(TAG, t.message.toString())
                }
            })
    }
}