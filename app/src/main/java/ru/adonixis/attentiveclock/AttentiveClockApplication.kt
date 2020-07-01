package ru.adonixis.attentiveclock

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class AttentiveClockApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setNightMode()
    }

    private fun setNightMode() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val mode = prefs.getInt(Settings.NIGHT_MODE, Settings.MODE_NIGHT_DEFAULT)
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}