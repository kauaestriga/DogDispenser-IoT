package com.example.dogdispenser.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dogdispenser.R
import com.example.dogdispenser.utils.MyClickListener

class SettingsAdapter(
    private val scheduleList: ArrayList<String>,
    private val enableList: ArrayList<Boolean>,
    private var myClickListener: MyClickListener
): RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_settings_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scheduleList[position], enableList[position], myClickListener)
    }

    override fun getItemCount() = scheduleList.size

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(schedule: String, enable: Boolean, myClickListener: MyClickListener) {
            val textView = itemView.findViewById<TextView>(R.id.tv_schudule)
            val switch = itemView.findViewById<SwitchCompat>(R.id.swt_enable)

            textView.text = schedule
            switch.isChecked = enable

            itemView.setOnLongClickListener {
                myClickListener.onLongClick(schedule)
                return@setOnLongClickListener true
            }
        }
    }
}

