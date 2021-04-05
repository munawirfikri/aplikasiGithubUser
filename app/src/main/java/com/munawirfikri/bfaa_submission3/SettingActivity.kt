package com.munawirfikri.bfaa_submission3

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.munawirfikri.bfaa_submission3.databinding.ActivitySettingBinding
import com.munawirfikri.bfaa_submission3.receiver.AlarmReceiver

class SettingActivity : AppCompatActivity(), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var alarmReceiver: AlarmReceiver
    private lateinit var mSharedPreferences: SharedPreferences

    companion object {
        const val PREFS_NAME = "SettingPref"
        private const val REMINDER = "reminder"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mSharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        binding.navBack.setOnClickListener(this)

        binding.scReminder.isChecked = mSharedPreferences.getBoolean(REMINDER, false)
        binding.scReminder.setOnCheckedChangeListener(this)
        alarmReceiver = AlarmReceiver()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.nav_back -> finish()
        }
    }

    private fun saveChange(value: Boolean) {
        val editor = mSharedPreferences.edit()
        editor.putBoolean(REMINDER, value)
        editor.apply()
    }


    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if(isChecked) {
                alarmReceiver.setAlarm(this,AlarmReceiver.TYPE,getString(R.string.alarm_message))
        }else {
                alarmReceiver.unSetAlarm(this)
        }
        saveChange(isChecked)
    }

}