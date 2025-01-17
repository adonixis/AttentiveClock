package ru.adonixis.attentiveclock.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.*
import android.graphics.Point
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RemoteViews
import ru.adonixis.attentiveclock.BuildConfig
import ru.adonixis.attentiveclock.R
import ru.adonixis.attentiveclock.activity.MainActivity
import ru.adonixis.attentiveclock.view.AnalogClockView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class AnalogClockWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val ACTION_UPDATE_WIDGET = "ru.adonixis.attentiveclock.ACTION_UPDATE_WIDGET"
        private const val LOG_TAG = "AttentiveClock"
    }

    private var receiver: BroadcastReceiver? = null
    private var alarmManager: AlarmManager? = null
    private var pendingIntent: PendingIntent? = null

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action!!.compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    if (BuildConfig.DEBUG) {
                        val calendar = Calendar.getInstance()
                        val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
                        val strTime = sdf.format(calendar.time)
                        Log.d(LOG_TAG, "AnalogClock onReceive: ACTION_TIME_TICK at $strTime")
                    }
                    updateWidget(context)
                }
            }
        }
        context.applicationContext.registerReceiver(receiver, IntentFilter(Intent.ACTION_TIME_TICK))
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            val remoteViews = RemoteViews(context.packageName, R.layout.analog_clock_widget_layout)
            remoteViews.setOnClickPendingIntent(R.id.analog_appwidget, pendingIntent)
            drawWidget(context, remoteViews)
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
            createAlarm(context)
        }
    }

    private fun drawWidget(context: Context, remoteViews: RemoteViews) {
        val analogClockView = AnalogClockView(context)
        analogClockView.isDrawingCacheEnabled = true
        analogClockView.setShowSeconds(false)
        analogClockView.setShowDialOuterShadow(false)
        analogClockView.setShowDialInnerShadow(true)

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val point = Point()
        display.getSize(point)
        val screenWidth = point.x
        val screenHeight = point.y

        val size = min(screenWidth, screenHeight)
        analogClockView.layout(0, 0, size, size)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        analogClockView.layoutParams = params
        analogClockView.buildDrawingCache()
        val bitmap = analogClockView.drawingCache
        remoteViews.setImageViewBitmap(R.id.imageView, bitmap)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action
        if (BuildConfig.DEBUG) {
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
            val strTime = sdf.format(calendar.time)
            Log.d(LOG_TAG, "AnalogClock onReceive: $action at $strTime")
        }
        when (action) {
            ACTION_UPDATE_WIDGET, Intent.ACTION_DATE_CHANGED, Intent.ACTION_TIMEZONE_CHANGED, Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIME_TICK, Intent.ACTION_BOOT_COMPLETED ->
                updateWidget(context)
        }
    }

    private fun updateWidget(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisAppWidget = ComponentName(context, AnalogClockWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
        onUpdate(context, appWidgetManager, appWidgetIds)
    }

    protected fun createAlarm(context: Context) {
        val newIntent = Intent(context, AnalogClockWidgetProvider::class.java)
        newIntent.action = ACTION_UPDATE_WIDGET
        pendingIntent = PendingIntent.getBroadcast(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val calendar = Calendar.getInstance()
        val now = System.currentTimeMillis()
        val delta = 60000 - now % 60000
        calendar.timeInMillis = now + delta

        if (alarmManager == null) {
            alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
        alarmManager!!.setExact(AlarmManager.RTC, calendar.timeInMillis, pendingIntent)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        if (alarmManager != null) {
            alarmManager!!.cancel(pendingIntent)
        }
        if (receiver != null) {
            context.applicationContext.unregisterReceiver(receiver)
        }
    }
}