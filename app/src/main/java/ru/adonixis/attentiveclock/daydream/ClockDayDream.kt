package ru.adonixis.attentiveclock.daydream

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.service.dreams.DreamService
import android.view.View
import android.view.animation.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager
import ru.adonixis.attentiveclock.R
import ru.adonixis.attentiveclock.Settings
import ru.adonixis.attentiveclock.view.AnalogClockView
import ru.adonixis.attentiveclock.view.DigitalClockView


class ClockDayDream : DreamService() {
    private var digitalClock: DigitalClockView? = null
    private var analogClock: AnalogClockView? = null
    private var clock: View? = null
    val animSetAppear = AnimationSet(true)
    val animSetDisappear = AnimationSet(true)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val mode = prefs.getInt(Settings.NIGHT_MODE, Settings.MODE_NIGHT_DEFAULT)
        AppCompatDelegate.setDefaultNightMode(mode)

        val nightModePreference = prefs.getBoolean(getString(R.string.key_night_mode_screen_saver), false)
        isScreenBright = !nightModePreference
        isInteractive = true
        isFullscreen = true

        val decorView = window.decorView
        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = flags
        setContentView(R.layout.daydream)
        digitalClock = findViewById(R.id.digitalClock)
        analogClock = findViewById(R.id.analogClock)

        val stylePreference = prefs.getString(getString(R.string.key_style_screen_saver), "")
        val styleValues = resources.getStringArray(R.array.style_screen_saver_values)
        when (stylePreference) {
            styleValues[0] -> {
                digitalClock?.visibility = View.VISIBLE
                analogClock?.visibility = View.GONE
            }
            styleValues[1] -> {
                digitalClock?.visibility = View.GONE
                analogClock?.visibility = View.VISIBLE
            }
            else -> {
                digitalClock?.visibility = View.VISIBLE
                analogClock?.visibility = View.GONE
            }
        }

        val secondsPreference = prefs.getBoolean(getString(R.string.key_seconds_screen_saver), true)
        analogClock?.setShowSeconds(secondsPreference)
        digitalClock?.setShowSeconds(secondsPreference)
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()

        setAnimation()

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action!!.compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    animateClock()
                }
            }
        }

        applicationContext.registerReceiver(receiver, IntentFilter(Intent.ACTION_TIME_TICK))
    }

    private fun setAnimation() {
        if (analogClock?.visibility == View.VISIBLE) {
            clock = analogClock!!
        } else {
            clock = digitalClock!!
        }

        val animAlpha = AlphaAnimation(0.0f, 1.0f)
        animAlpha.duration = 3000
        animAlpha.fillBefore = true
        animAlpha.fillAfter = true
        clock?.startAnimation(animAlpha)

        animSetDisappear.interpolator = AccelerateInterpolator()
        animSetDisappear.fillAfter = true
        animSetDisappear.isFillEnabled = true

        val animScaleDisappear = ScaleAnimation(
            1.0f, 0.0f,
            1.0f, 0.0f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        animScaleDisappear.duration = 1000
        animScaleDisappear.fillAfter = true

        val animAlphaDisappear = AlphaAnimation(1.0f, 0.0f)
        animAlphaDisappear.duration = 1000
        animAlphaDisappear.fillAfter = true

        animSetDisappear.addAnimation(animScaleDisappear)
        animSetDisappear.addAnimation(animAlphaDisappear)

        animSetAppear.interpolator = DecelerateInterpolator()
        animSetAppear.fillAfter = true
        animSetAppear.isFillEnabled = true

        val animScaleAppear = ScaleAnimation(
            0.0f, 1.0f,
            0.0f, 1.0f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        animScaleAppear.duration = 1000
        animScaleAppear.fillAfter = true

        val animAlphaAppear = AlphaAnimation(0.0f, 1.0f)
        animAlphaAppear.duration = 1000
        animAlphaAppear.fillAfter = true

        animSetAppear.addAnimation(animScaleAppear)
        animSetAppear.addAnimation(animAlphaAppear)
    }

    private fun animateClock() {
        val randomX = (0..10).random() * 0.1f
        val randomY = (1..9).random() * 0.1f

        val clockParams: ConstraintLayout.LayoutParams = clock?.layoutParams as ConstraintLayout.LayoutParams

        animSetDisappear.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                clockParams.horizontalBias = randomX
                clockParams.verticalBias = randomY
                clock?.layoutParams = clockParams
                clock?.startAnimation(animSetAppear)
            }
        })
        clock?.startAnimation(animSetDisappear)
    }

}