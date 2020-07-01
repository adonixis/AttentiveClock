package ru.adonixis.attentiveclock.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import ru.adonixis.attentiveclock.R
import ru.adonixis.attentiveclock.util.Utils.convertDpToPixel
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class AnalogClockView: View {
    private var mHeight = 0
    private var mWidth = 0
    private var mDiameterDialOuter = 0f
    private var mRadiusDialOuter = 0f
    private var mRadiusDialInner = 0f
    private var mRadiusCap = 0f
    private var mAngle = 0.0
    private var mCenterX = 0f
    private var mCenterY = 0f
    private var mPadding = 0f
    private var mMinimumSide = 0
    private var mHour = 0
    private var mMinute = 0
    private var mSecond = 0
    private var mHourHandHeight = 0f
    private var mHourHandWidth = 0f
    private var mMinuteHandHeight = 0f
    private var mMinuteHandWidth = 0f
    private var mSecondHandHeight = 0f
    private var mSecondHandWidth = 0f
    private var mDialStrokeHeight = 0f
    private var mDialStrokeWidth = 0f
    private var mHourHandLedgeHeight = 0f
    private var mMinuteHandLedgeHeight = 0f
    private var mSecondHandLedgeHeight = 0f
    private var mPaintDialOuter: Paint? = null
    private var mPaintDialInner: Paint? = null
    private var mPaintDialStrokes: Paint? = null
    private var mPaintHourHand: Paint? = null
    private var mPaintMinuteHand: Paint? = null
    private var mPaintSecondHand: Paint? = null
    private var mPaintCap: Paint? = null
    private var mIsInit = false

    private var mShowSeconds = true
    private var mShowDialOuterShadow = true
    private var mShowDialInnerShadow = true
    private var mCalendar: Calendar = Calendar.getInstance()
    @ColorInt private var mColorHourHand = 0
    @ColorInt private var mColorMinuteHand = 0
    @ColorInt private var mColorSecondHand = 0
    @ColorInt private var mColorDialStrokes = 0
    @ColorInt private var mColorDialOuter = 0
    @ColorInt private var mColorDialOuterShadow = 0
    @ColorInt private var mColorDialInner = 0
    @ColorInt private var mColorDialInnerShadow = 0

    companion object {
        private const val DIAL_OUTER_DIAMETER = 337.0f
        private const val DIAL_INNER_DIAMETER = 257.0f
        private const val HOUR_HAND_HEIGHT = 80.0f
        private const val HOUR_HAND_WIDTH = 5.0f
        private const val HOUR_HAND_LEDGE_HEIGHT = 19f
        private const val MINUTE_HAND_HEIGHT = 104.0f
        private const val MINUTE_HAND_WIDTH = 4.0f
        private const val MINUTE_HAND_LEDGE_HEIGHT = 19f
        private const val SECOND_HAND_HEIGHT = 154.0f
        private const val SECOND_HAND_WIDTH = 4.0f
        private const val SECOND_HAND_LEDGE_HEIGHT = 17.0f
        private const val CAP_DIAMETER = 12.0f
        private const val DIAL_STROKE_HEIGHT = 4.0f
        private const val DIAL_STROKE_WIDTH = 4.0f
    }

    constructor(context: Context) : this(context, null) {}
    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs,
            R.styleable.AnalogClockView
        )
        mShowSeconds = typedArray.getBoolean(R.styleable.AnalogClockView_showSeconds, true)
        mShowDialOuterShadow = typedArray.getBoolean(R.styleable.AnalogClockView_showDialOuterShadow, true)
        mShowDialInnerShadow = typedArray.getBoolean(R.styleable.AnalogClockView_showDialInnerShadow, true)
        mColorHourHand = typedArray.getColor(
            R.styleable.AnalogClockView_colorHourHand,
            Color.parseColor("#0C0F1F")
        )
        mColorMinuteHand = typedArray.getColor(
            R.styleable.AnalogClockView_colorMinuteHand,
            Color.parseColor("#9FA7BC")
        )
        mColorSecondHand = typedArray.getColor(
            R.styleable.AnalogClockView_colorSecondHand,
            Color.parseColor("#D81F72")
        )
        mColorDialOuter = typedArray.getColor(
            R.styleable.AnalogClockView_colorDialOuter,
            Color.parseColor("#E7EEFB")
        )
        mColorDialOuterShadow = typedArray.getColor(
            R.styleable.AnalogClockView_colorDialOuterShadow,
            Color.parseColor("#C4D4E7")
        )
        mColorDialInner = typedArray.getColor(
            R.styleable.AnalogClockView_colorDialInner,
            Color.parseColor("#EDF1FB")
        )
        mColorDialInnerShadow = typedArray.getColor(
            R.styleable.AnalogClockView_colorDialInnerShadow,
            Color.parseColor("#59C4D4E7")
        )
        mColorDialStrokes = typedArray.getColor(
            R.styleable.AnalogClockView_colorDialStrokes,
            Color.parseColor("#FFFFFF")
        )
        typedArray.recycle()
    }

    fun setShowSeconds(showSeconds: Boolean) {
        this.mShowSeconds = showSeconds
        invalidate()
    }

    fun setShowDialOuterShadow(showDialOuterShadow: Boolean) {
        this.mShowDialOuterShadow = showDialOuterShadow
        invalidate()
    }

    fun setShowDialInnerShadow(showDialInnerShadow: Boolean) {
        this.mShowDialInnerShadow = showDialInnerShadow
        invalidate()
    }

    private fun init() {
        mHeight = height
        mWidth = width

        if (mShowDialOuterShadow) {
            mPadding = convertDpToPixel(52.0f, context)
        } else {
            mPadding = 0.0f
        }
        mCenterX = mWidth / 2.0f
        mCenterY = mHeight / 2.0f
        mMinimumSide = min(mHeight, mWidth)
        mDiameterDialOuter = mMinimumSide - mPadding * 2
        mRadiusDialOuter = mDiameterDialOuter / 2.0f
        mRadiusDialInner = mRadiusDialOuter / (DIAL_OUTER_DIAMETER / DIAL_INNER_DIAMETER)
        mRadiusCap = mRadiusDialOuter / (DIAL_OUTER_DIAMETER / CAP_DIAMETER)
        mAngle = Math.PI / 30.0f - Math.PI / 2.0f
        mHourHandHeight = mDiameterDialOuter / (DIAL_OUTER_DIAMETER / HOUR_HAND_HEIGHT)
        mHourHandWidth = mDiameterDialOuter / (DIAL_OUTER_DIAMETER / HOUR_HAND_WIDTH)
        mHourHandLedgeHeight = mDiameterDialOuter / (DIAL_OUTER_DIAMETER / HOUR_HAND_LEDGE_HEIGHT)
        mMinuteHandHeight = mDiameterDialOuter / (DIAL_OUTER_DIAMETER / MINUTE_HAND_HEIGHT)
        mMinuteHandWidth = mDiameterDialOuter / (DIAL_OUTER_DIAMETER / MINUTE_HAND_WIDTH)
        mMinuteHandLedgeHeight = mDiameterDialOuter / (DIAL_OUTER_DIAMETER / MINUTE_HAND_LEDGE_HEIGHT)
        mSecondHandHeight = mDiameterDialOuter / (DIAL_OUTER_DIAMETER / SECOND_HAND_HEIGHT)
        mSecondHandWidth = mDiameterDialOuter / (DIAL_OUTER_DIAMETER / SECOND_HAND_WIDTH)
        mSecondHandLedgeHeight = mDiameterDialOuter / (DIAL_OUTER_DIAMETER / SECOND_HAND_LEDGE_HEIGHT)
        mDialStrokeHeight = mDiameterDialOuter / (DIAL_OUTER_DIAMETER / DIAL_STROKE_HEIGHT)
        mDialStrokeWidth = mDiameterDialOuter / (DIAL_OUTER_DIAMETER / DIAL_STROKE_WIDTH)

        mPaintDialOuter = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintDialOuter!!.color = mColorDialOuter
        if (mShowDialOuterShadow) {
            mPaintDialOuter!!.setShadowLayer(100f, 25f, 0f, mColorDialOuterShadow)
        }

        mPaintDialInner = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintDialInner!!.color = mColorDialInner
        if (mShowDialInnerShadow) {
            mPaintDialInner!!.setShadowLayer(20f, 10f, 0f, mColorDialInnerShadow)
        }

        if (Build.VERSION.SDK_INT >= 28){
            setLayerType(LAYER_TYPE_HARDWARE, mPaintDialOuter)
            setLayerType(LAYER_TYPE_HARDWARE, mPaintDialInner)
        } else{
            setLayerType(LAYER_TYPE_SOFTWARE, mPaintDialOuter)
            setLayerType(LAYER_TYPE_SOFTWARE, mPaintDialInner)
        }

        if (mShowDialOuterShadow) {
            mPaintDialOuter!!.setShadowLayer(100f, 25f, 0f, mColorDialOuterShadow)
        } else {
            mPaintDialOuter!!.clearShadowLayer()
        }

        if (mShowDialInnerShadow) {
            mPaintDialInner!!.setShadowLayer(20f, 10f, 0f, mColorDialInnerShadow)
        } else {
            mPaintDialInner!!.clearShadowLayer()
        }

        mPaintDialStrokes = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintDialStrokes!!.color = mColorDialStrokes
        mPaintDialStrokes!!.style = Paint.Style.FILL_AND_STROKE
        mPaintDialStrokes!!.strokeWidth = mDialStrokeWidth
        mPaintDialStrokes!!.strokeCap = Paint.Cap.ROUND
        mPaintHourHand = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintHourHand!!.color = mColorHourHand
        mPaintHourHand!!.style = Paint.Style.FILL_AND_STROKE
        mPaintHourHand!!.strokeWidth = mHourHandWidth
        mPaintHourHand!!.strokeCap = Paint.Cap.ROUND
        mPaintMinuteHand = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintMinuteHand!!.color = mColorMinuteHand
        mPaintMinuteHand!!.style = Paint.Style.FILL_AND_STROKE
        mPaintMinuteHand!!.strokeWidth = mMinuteHandWidth
        mPaintMinuteHand!!.strokeCap = Paint.Cap.ROUND
        mPaintSecondHand = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintSecondHand!!.color = mColorSecondHand
        mPaintSecondHand!!.style = Paint.Style.FILL_AND_STROKE
        mPaintSecondHand!!.strokeWidth = mSecondHandWidth
        mPaintSecondHand!!.strokeCap = Paint.Cap.ROUND
        mPaintCap = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintCap!!.color = mColorSecondHand
        mPaintCap!!.style = Paint.Style.FILL
        mPaintCap!!.strokeWidth = 0f
        mPaintCap!!.strokeCap = Paint.Cap.BUTT
        mIsInit = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = measuredHeight
        val dimen = min(height, width)
        setMeasuredDimension(dimen, dimen)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!mIsInit) {
            init()
        }
        drawDialOuter(canvas)
        drawDialInner(canvas)
        drawDialStrokes(canvas)
        drawHands(canvas)
        drawCap(canvas)
        postInvalidateDelayed(50)
    }

    private fun drawDialOuter(canvas: Canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mRadiusDialOuter, mPaintDialOuter!!)
    }

    private fun drawDialInner(canvas: Canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mRadiusDialInner, mPaintDialInner!!)
    }

    private fun drawDialStrokes(canvas: Canvas) {
        for (i in 1..12) {
            val angle = Math.PI / 6.0f * (i - 3)
            val delta = (mRadiusDialOuter - mRadiusDialInner) / 2 - mDialStrokeHeight / 2
            val x = (mCenterX + cos(angle) * (mRadiusDialInner + delta)).toFloat()
            val y = (mCenterY + sin(angle) * (mRadiusDialInner + delta)).toFloat()
            canvas.drawLine(
                x,
                y,
                (x + cos(angle) * mDialStrokeHeight).toFloat(),
                (y + sin(angle) * mDialStrokeHeight).toFloat(),
                mPaintDialStrokes!!
            )
        }
    }

    private fun drawHands(canvas: Canvas) {
        mCalendar = Calendar.getInstance()
        mSecond = mCalendar.get(Calendar.SECOND)
        mMinute = mCalendar.get(Calendar.MINUTE)
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY)
        mHour = if (mHour > 12) mHour - 12 else mHour
        drawHourHand(canvas, (mHour + mMinute / 60.0) * 5.0f)
        drawMinuteHand(canvas, mMinute + mSecond / 60.0f.toDouble())
        if (mShowSeconds) {
            drawSecondHand(canvas, mSecond.toDouble())
        }
    }

    private fun drawHourHand(canvas: Canvas, location: Double) {
        mAngle = Math.PI * location / 30.0f - Math.PI / 2.0f
        canvas.drawLine(
            (mCenterX - cos(mAngle) * mHourHandLedgeHeight).toFloat(),
            (mCenterY - sin(mAngle) * mHourHandLedgeHeight).toFloat(),
            (mCenterX + cos(mAngle) * (mHourHandHeight - mHourHandLedgeHeight)).toFloat(),
            (mCenterY + sin(mAngle) * (mHourHandHeight - mHourHandLedgeHeight)).toFloat(),
            mPaintHourHand!!
        )
    }

    private fun drawMinuteHand(canvas: Canvas, location: Double) {
        mAngle = Math.PI * location / 30.0f - Math.PI / 2.0f
        canvas.drawLine(
            (mCenterX - cos(mAngle) * mMinuteHandLedgeHeight).toFloat(),
            (mCenterY - sin(mAngle) * mMinuteHandLedgeHeight).toFloat(),
            (mCenterX + cos(mAngle) * (mMinuteHandHeight - mMinuteHandLedgeHeight)).toFloat(),
            (mCenterY + sin(mAngle) * (mMinuteHandHeight - mMinuteHandLedgeHeight)).toFloat(),
            mPaintMinuteHand!!
        )
    }

    private fun drawSecondHand(canvas: Canvas, location: Double) {
        mAngle = Math.PI * location / 30.0f - Math.PI / 2.0f
        canvas.drawLine(
            (mCenterX - cos(mAngle) * mSecondHandLedgeHeight).toFloat(),
            (mCenterY - sin(mAngle) * mSecondHandLedgeHeight).toFloat(),
            (mCenterX + cos(mAngle) * (mSecondHandHeight - mSecondHandLedgeHeight)).toFloat(),
            (mCenterY + sin(mAngle) * (mSecondHandHeight - mSecondHandLedgeHeight)).toFloat(),
            mPaintSecondHand!!
        )
    }

    private fun drawCap(canvas: Canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mRadiusCap, mPaintCap!!)
    }

}