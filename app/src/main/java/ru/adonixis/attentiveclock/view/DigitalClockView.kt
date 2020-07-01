package ru.adonixis.attentiveclock.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.ColorUtils
import ru.adonixis.attentiveclock.R
import java.util.*

class DigitalClockView: AppCompatTextView{

    private var mShowSeconds = true
    private var mShowAMPM = true
    private var mShowDigitsInSequence = true
    private var mShowHoursEqualsMinutes = true
    private var mShowPalindrome = true
    private var mShowTimeEqualsDate = true
    private var mShowHistoricalDates = false
    private var mIsInit = false

    private var sb: StringBuilder = StringBuilder()
    @ColorInt private var mainTextColor: Int = 0
    private var mainTextColorSpan1: ForegroundColorSpan
    private var mainTextColorSpan2: ForegroundColorSpan
    private var mainTextColorSpan3: ForegroundColorSpan
    @ColorInt private var accentTextColor: Int = 0
    private var accentTextColorSpan1: ForegroundColorSpan
    private var accentTextColorSpan2: ForegroundColorSpan
    private var accentTextColorSpan3: ForegroundColorSpan
    private var accentTextColorSpanAlpha90: ForegroundColorSpan
    private var accentTextColorSpanAlpha80: ForegroundColorSpan
    private var accentTextColorSpanAlpha70: ForegroundColorSpan
    private var accentTextColorSpanAlpha60: ForegroundColorSpan
    private var accentTextColorSpanAlpha50: ForegroundColorSpan
    private var accentTextColorSpanAlpha40: ForegroundColorSpan
    private var accentTextColorSpanAlpha30: ForegroundColorSpan
    private var accentTextColorSpanAlpha20: ForegroundColorSpan
    private var accentTextColorSpanAlpha10: ForegroundColorSpan
    private var relativeSizeSpan70: RelativeSizeSpan
    private var relativeSizeSpan25: RelativeSizeSpan

    private lateinit var mCalendar: Calendar
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mMonthName: String = ""
    private var mDayOfWeekName: String = ""
    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private var mSecond: Int = 0
    private var mTimeText = ""
    private lateinit var mDigitalClockSpan: Spannable

    private var mDescription: String = ""
    private var mDescriptionLast: String = ""

    companion object {
        private const val TEXT_SIZE = 64.0f
    }

    constructor(context: Context) : this(context, null) {}
    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DigitalClockView)
        mShowSeconds = typedArray.getBoolean(R.styleable.DigitalClockView_showSeconds, true)
        mShowAMPM = typedArray.getBoolean(R.styleable.DigitalClockView_showAMPM, true)
        mShowDigitsInSequence = typedArray.getBoolean(R.styleable.DigitalClockView_showDigitsInSequence, true)
        mShowHoursEqualsMinutes = typedArray.getBoolean(R.styleable.DigitalClockView_showHoursEqualsMinutes, true)
        mShowPalindrome = typedArray.getBoolean(R.styleable.DigitalClockView_showPalindrome, true)
        mShowTimeEqualsDate = typedArray.getBoolean(R.styleable.DigitalClockView_showTimeEqualsDate, true)
        mShowHistoricalDates = typedArray.getBoolean(R.styleable.DigitalClockView_showHistoricalDates, false)

        mainTextColor = typedArray.getColor(R.styleable.DigitalClockView_colorMain, Color.parseColor("#0C0F1F"))
        mainTextColorSpan1 = ForegroundColorSpan(mainTextColor)
        mainTextColorSpan2 = ForegroundColorSpan(mainTextColor)
        mainTextColorSpan3 = ForegroundColorSpan(mainTextColor)
        accentTextColor = typedArray.getColor(R.styleable.DigitalClockView_colorAccent, Color.parseColor("#D81F72"))
        accentTextColorSpan1 = ForegroundColorSpan(accentTextColor)
        accentTextColorSpan2 = ForegroundColorSpan(accentTextColor)
        accentTextColorSpan3 = ForegroundColorSpan(accentTextColor)
        accentTextColorSpanAlpha90 = ForegroundColorSpan(ColorUtils.setAlphaComponent(accentTextColor, 230))
        accentTextColorSpanAlpha80 = ForegroundColorSpan(ColorUtils.setAlphaComponent(accentTextColor, 204))
        accentTextColorSpanAlpha70 = ForegroundColorSpan(ColorUtils.setAlphaComponent(accentTextColor, 179))
        accentTextColorSpanAlpha60 = ForegroundColorSpan(ColorUtils.setAlphaComponent(accentTextColor, 153))
        accentTextColorSpanAlpha50 = ForegroundColorSpan(ColorUtils.setAlphaComponent(accentTextColor, 128))
        accentTextColorSpanAlpha40 = ForegroundColorSpan(ColorUtils.setAlphaComponent(accentTextColor, 102))
        accentTextColorSpanAlpha30 = ForegroundColorSpan(ColorUtils.setAlphaComponent(accentTextColor, 77))
        accentTextColorSpanAlpha20 = ForegroundColorSpan(ColorUtils.setAlphaComponent(accentTextColor, 51))
        accentTextColorSpanAlpha10 = ForegroundColorSpan(ColorUtils.setAlphaComponent(accentTextColor, 26))

        relativeSizeSpan70 = RelativeSizeSpan(0.7f)
        relativeSizeSpan25 = RelativeSizeSpan(0.25f)

        typedArray.recycle()
    }

    fun setShowSeconds(showSeconds: Boolean) {
        this.mShowSeconds = showSeconds
        invalidate()
    }

    fun setShowAMPM(showAMPM: Boolean) {
        this.mShowAMPM = showAMPM
        invalidate()
    }

    fun setShowDigitsInSequence(showDigitsInSequence: Boolean) {
        this.mShowDigitsInSequence = showDigitsInSequence
        invalidate()
    }

    fun setShowHoursEqualsMinutes(showHoursEqualsMinutes: Boolean) {
        this.mShowHoursEqualsMinutes = showHoursEqualsMinutes
        invalidate()
    }

    fun setShowPalindrome(showPalindrome: Boolean) {
        this.mShowPalindrome = showPalindrome
        invalidate()
    }

    fun setShowTimeEqualsDate(showTimeEqualsDate: Boolean) {
        this.mShowTimeEqualsDate = showTimeEqualsDate
        invalidate()
    }

    fun setShowHistoricalDates(showHistoricalDates: Boolean) {
        this.mShowHistoricalDates = showHistoricalDates
        invalidate()
    }

    fun setMainTextColor(@ColorInt mainTextColor: Int) {
        this.mainTextColor = mainTextColor
        mainTextColorSpan1 = ForegroundColorSpan(mainTextColor)
        mainTextColorSpan2 = ForegroundColorSpan(mainTextColor)
        mainTextColorSpan3 = ForegroundColorSpan(mainTextColor)
        invalidate()
    }

    private fun pad(c: Int): String? {
        return if (c >= 10) c.toString() else "0$c"
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 1100
        val desiredHeight = 350

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val width: Int
        val height: Int

        width = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                Math.min(desiredWidth, widthSize)
            }
            else -> {
                desiredWidth
            }
        }

        height = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                Math.min(desiredHeight, heightSize)
            }
            else -> {
                desiredHeight
            }
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!mIsInit) {
            init()
        }
        drawTimeAndDate()
        postInvalidateDelayed(50)
    }

    private fun init() {
        textSize = TEXT_SIZE
        gravity = Gravity.CENTER
        mIsInit = true
    }

    private fun drawTimeAndDate() {
        mCalendar = Calendar.getInstance()
        mYear = mCalendar.get(Calendar.YEAR)
        mMonth = mCalendar.get(Calendar.MONTH)
        mMonthName = mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())!!
        mDayOfWeekName = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())!!
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

        var dayAndMonth: String
        if (Locale.getDefault().toString() == "ru_RU") {
            dayAndMonth = "$mDay $mMonthName"
        } else {
            dayAndMonth = "$mMonthName $mDay"
        }

        mHour = mCalendar.get(Calendar.HOUR_OF_DAY)
        if (!DateFormat.is24HourFormat(context) && mHour > 12) {
            mHour -= 12
        }

        mMinute = mCalendar.get(Calendar.MINUTE)
        mSecond = mCalendar.get(Calendar.SECOND)

        sb.clear()
        if (mShowSeconds) {
            if (DateFormat.is24HourFormat(context) || !mShowAMPM) { //16:27:45 or 04:27:45
                mTimeText = sb
                    .append(pad(mHour))
                    .append(":")
                    .append(pad(mMinute))
                    .append(":")
                    .append(pad(mSecond))
                    .append('\n')
                    .append(mDayOfWeekName)
                    .append(", ")
                    .append(dayAndMonth)
                    .toString()

                mDigitalClockSpan = SpannableString.valueOf(mTimeText)
                mDigitalClockSpan.setSpan(mainTextColorSpan3, 5, mTimeText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                mDigitalClockSpan.setSpan(relativeSizeSpan70, 5, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                mDigitalClockSpan.setSpan(relativeSizeSpan25, 8, mTimeText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else { //04:27:45 PM
                var hourAMPM: String
                if (mCalendar.get(Calendar.HOUR_OF_DAY) > 12) {
                    hourAMPM = "PM"
                } else {
                    hourAMPM = "AM"
                }
                mTimeText = sb
                    .append(pad(mHour))
                    .append(":")
                    .append(pad(mMinute))
                    .append(":")
                    .append(pad(mSecond))
                    .append(" ")
                    .append(hourAMPM)
                    .append('\n')
                    .append(mDayOfWeekName)
                    .append(", ")
                    .append(dayAndMonth)
                    .toString()

                mDigitalClockSpan = SpannableString.valueOf(mTimeText)
                mDigitalClockSpan.setSpan(mainTextColorSpan3, 5, mTimeText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                mDigitalClockSpan.setSpan(relativeSizeSpan70, 5, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                mDigitalClockSpan.setSpan(relativeSizeSpan25, 11, mTimeText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } else {
            if (DateFormat.is24HourFormat(context) || !mShowAMPM) { //16:27 or 04:27
                mTimeText = sb
                    .append(pad(mHour))
                    .append(":")
                    .append(pad(mMinute))
                    .append('\n')
                    .append(mDayOfWeekName)
                    .append(", ")
                    .append(dayAndMonth)
                    .toString()

                mDigitalClockSpan = SpannableString.valueOf(mTimeText)
                mDigitalClockSpan.setSpan(relativeSizeSpan25, 5, mTimeText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                mDigitalClockSpan.setSpan(mainTextColorSpan3, 5, mTimeText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else { //04:27 PM
                var hourAMPM: String
                if (mCalendar.get(Calendar.HOUR_OF_DAY) > 12) {
                    hourAMPM = "PM"
                } else {
                    hourAMPM = "AM"
                }
                mTimeText = sb
                    .append(pad(mHour))
                    .append(":")
                    .append(pad(mMinute))
                    .append(" ")
                    .append(hourAMPM)
                    .append('\n')
                    .append(mDayOfWeekName)
                    .append(", ")
                    .append(dayAndMonth)
                    .toString()

                mDigitalClockSpan = SpannableString.valueOf(mTimeText)
                mDigitalClockSpan.setSpan(mainTextColorSpan3, 5, mTimeText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                mDigitalClockSpan.setSpan(relativeSizeSpan70, 5, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                mDigitalClockSpan.setSpan(relativeSizeSpan25, 8, mTimeText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        val historicalDatesEntries = resources.getStringArray(R.array.historical_dates_entries)
        val historicalDatesValues = resources.getStringArray(R.array.historical_dates_values)

        if (mShowHoursEqualsMinutes && (
                    mHour == 0 && mMinute == 0 ||
                    mHour == 11 && mMinute == 11 ||
                    mHour == 22 && mMinute == 22)
        ) {
            mDigitalClockSpan.setSpan(accentTextColorSpan1, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mDescription = ""
            if (mDescriptionLast != mDescription) {
                listener?.onDescriptionUpdate(mDescription)
                mDescriptionLast = mDescription
            }
        } else if (mShowHoursEqualsMinutes && (mHour == mMinute)) {
            //same hours and minutes
            mDigitalClockSpan.setSpan(accentTextColorSpan1, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mDigitalClockSpan.setSpan(mainTextColorSpan1, 1, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mDigitalClockSpan.setSpan(accentTextColorSpan2, 3, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mDigitalClockSpan.setSpan(mainTextColorSpan2, 4, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mDescription = ""
            if (mDescriptionLast != mDescription) {
                listener?.onDescriptionUpdate(mDescription)
                mDescriptionLast = mDescription
            }
        } else if (mShowDigitsInSequence && (
                    mHour == 1 && mMinute == 23 ||
                    mHour == 12 && mMinute == 34 ||
                    mHour == 23 && mMinute == 45)
        ) {
            //consecutive hours and minutes
            mDigitalClockSpan.setSpan(accentTextColorSpanAlpha30, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mDigitalClockSpan.setSpan(accentTextColorSpanAlpha50, 1, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mDigitalClockSpan.setSpan(accentTextColorSpanAlpha70, 3, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mDigitalClockSpan.setSpan(accentTextColorSpan1, 4, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mDescription = ""
            if (mDescriptionLast != mDescription) {
                listener?.onDescriptionUpdate(mDescription)
                mDescriptionLast = mDescription
            }
        } else if (mShowPalindrome && pad(mHour) == pad(mMinute)?.reversed()) {
            //palindrome hours and minutes
            mDigitalClockSpan.setSpan(accentTextColorSpan1, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mDigitalClockSpan.setSpan(mainTextColorSpan1, 1, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mDigitalClockSpan.setSpan(accentTextColorSpan2, 4, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mDescription = ""
            if (mDescriptionLast != mDescription) {
                listener?.onDescriptionUpdate(mDescription)
                mDescriptionLast = mDescription
            }
        } else if (mShowTimeEqualsDate && (mHour == mDay && mMinute == mMonth + 1)) {
            if (mShowSeconds) {
                if (DateFormat.is24HourFormat(context) || !mShowAMPM) { //16:27:45 or 04:27:45
                    mDigitalClockSpan.setSpan(accentTextColorSpan1, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    mDigitalClockSpan.setSpan(mainTextColorSpan1, 2, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    mDigitalClockSpan.setSpan(accentTextColorSpan2, 13, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    mDigitalClockSpan.setSpan(mainTextColorSpan2, 16, mTimeText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else { //04:27:45 PM
                    mDigitalClockSpan.setSpan(accentTextColorSpan1, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    mDigitalClockSpan.setSpan(mainTextColorSpan1, 2, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    mDigitalClockSpan.setSpan(accentTextColorSpan2, 16, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    mDigitalClockSpan.setSpan(mainTextColorSpan2, 19, mTimeText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else {
                if (DateFormat.is24HourFormat(context) || !mShowAMPM) { //16:27 or 04:27
                    mDigitalClockSpan.setSpan(accentTextColorSpan1, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    mDigitalClockSpan.setSpan(mainTextColorSpan1, 2, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    mDigitalClockSpan.setSpan(accentTextColorSpan2, 10, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    mDigitalClockSpan.setSpan(mainTextColorSpan2, 13, mTimeText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else { //04:27
                    mDigitalClockSpan.setSpan(accentTextColorSpan1, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    mDigitalClockSpan.setSpan(mainTextColorSpan1, 2, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    mDigitalClockSpan.setSpan(accentTextColorSpan2, 13, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    mDigitalClockSpan.setSpan(mainTextColorSpan2, 16, mTimeText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            mDescription = ""
            if (mDescriptionLast != mDescription) {
                listener?.onDescriptionUpdate(mDescription)
                mDescriptionLast = mDescription
            }
        } else if (mShowHistoricalDates && historicalDatesEntries.contains(pad(mHour) + pad(mMinute))) {
            val index = historicalDatesEntries.indexOf(pad(mHour) + pad(mMinute))
            val mDescription = historicalDatesValues[index]
            setTextColor(accentTextColor)
            if (mDescriptionLast != mDescription) {
                listener?.onDescriptionUpdate(mDescription)
                mDescriptionLast = mDescription
            }
        } else {
            setTextColor(mainTextColor)
            mDescription = ""
            if (mDescriptionLast != mDescription) {
                listener?.onDescriptionUpdate(mDescription)
                mDescriptionLast = mDescription
            }
        }

        text = mDigitalClockSpan
    }

    private var listener: DescriptionUpdateListener? = null

    fun setDescriptionUpdateListener(listener: DescriptionUpdateListener?) {
        this.listener = listener
    }

    interface DescriptionUpdateListener {
        fun onDescriptionUpdate(description: String?)
    }

}