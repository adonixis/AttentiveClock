package ru.adonixis.attentiveclock.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_settings.*
import ru.adonixis.attentiveclock.R
import ru.adonixis.attentiveclock.Settings


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.settings,
                    SettingsFragment()
                )
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)

            val themePreference = findPreference<ListPreference>(getString(R.string.key_theme))
            themePreference?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val index: Int = themePreference?.findIndexOfValue(newValue.toString())!!
                    when (index) {
                        0 -> {
                            setNightMode(Settings.MODE_NIGHT_DEFAULT)
                        }
                        1 -> {
                            setNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        }
                        2 -> {
                            setNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        }
                        else -> {
                            setNightMode(Settings.MODE_NIGHT_DEFAULT)
                        }
                    }

                    true
                }
        }

        private fun setNightMode(mode: Int) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            prefs.edit().putInt(Settings.NIGHT_MODE, mode).apply()
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }
}