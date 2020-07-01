package ru.adonixis.attentiveclock.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.adonixis.attentiveclock.R
import ru.adonixis.attentiveclock.view.DigitalClockView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onResume() {
        super.onResume()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val stylePreference = prefs.getString(getString(R.string.key_style), "")
        val styleValues = resources.getStringArray(R.array.style_values)
        when (stylePreference) {
            styleValues[0] -> {
                showClock(0)
            }
            styleValues[1] -> {
                showClock(1)
            }
            styleValues[2] -> {
                showClock(2)
            }
            else -> {
                showClock(0)
            }
        }

        val secondsPreference = prefs.getBoolean(getString(R.string.key_seconds), true)
        analogClock.setShowSeconds(secondsPreference)
        digitalClock.setShowSeconds(secondsPreference)
        val digitsInSequencePreference = prefs.getBoolean(getString(R.string.key_digits_in_sequence), true)
        digitalClock.setShowDigitsInSequence(digitsInSequencePreference)
        val hoursAndMinutesPreference = prefs.getBoolean(getString(R.string.key_hours_and_minutes), true)
        digitalClock.setShowHoursEqualsMinutes(hoursAndMinutesPreference)
        val palindromePreference = prefs.getBoolean(getString(R.string.key_palindrome), true)
        digitalClock.setShowPalindrome(palindromePreference)
        val timeAndDatePreference = prefs.getBoolean(getString(R.string.key_time_and_date), true)
        digitalClock.setShowTimeEqualsDate(timeAndDatePreference)
        val historicalDatesPreference = prefs.getBoolean(getString(R.string.key_historical_dates), true)
        digitalClock.setShowHistoricalDates(historicalDatesPreference)

        digitalClock.setDescriptionUpdateListener(object : DigitalClockView.DescriptionUpdateListener {
            override fun onDescriptionUpdate(description: String?) {
                tvDescription.text = description
            }
        })
    }

    private fun showClock(mode: Int) {
        when (mode) {
            0 -> {
                analogClock.visibility = View.VISIBLE
                digitalClock.visibility = View.VISIBLE
                tvDescription.visibility = View.VISIBLE
            }
            1 -> {
                analogClock.visibility = View.GONE
                digitalClock.visibility = View.VISIBLE
                tvDescription.visibility = View.VISIBLE
            }
            2 -> {
                analogClock.visibility = View.VISIBLE
                digitalClock.visibility = View.GONE
                tvDescription.visibility = View.GONE
            }
            else -> {
                analogClock.visibility = View.VISIBLE
                digitalClock.visibility = View.VISIBLE
                tvDescription.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_screen_saver -> {
                showScreenSaver()
                true
            }
            R.id.action_settings -> {
                showSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showScreenSaver() {
        val intent = Intent(Settings.ACTION_DREAM_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun showSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

}