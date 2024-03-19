package com.example.analogclock

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import java.lang.StrictMath.min
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin


class ClockView : View {
    private var coefficient = 0
    private var height = 0
    private var width = 0
    private var padding = 0
    private var fontSize = 0
    private val numeralSpacing = 0
    private var hourHandTruncation = 0
    private var minuteHandTruncation = 0
    private var radius = 0
    private var paint: Paint? = null
    private var isInit = false
    private val numbers = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    private val rect = Rect()
    private var radiusForDots = 0.0
    private var radiusOfSmallDots = 0f
    private var radiusOfBigDots = 0f

    private var shadowRadius = 0f
    private var shadowDx = 0f
    private var shadowDy = 0f
    private var shadowColor = Color.BLACK

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun initClock() {
        height = getHeight()
        width = getWidth()
        val min = height.coerceAtMost(width)
        fontSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, min / 20f,
            resources.displayMetrics
        ).toInt()
        coefficient = fontSize
        padding =
            if (coefficient < 20) numeralSpacing + (coefficient * 1.5).toInt()
            else numeralSpacing + coefficient
        radius = min / 2 - padding - coefficient / 4
        hourHandTruncation = min / 10
        minuteHandTruncation = min / 15
        paint = Paint()
        isInit = true
        radiusForDots = min(height, width) / 2 - coefficient / 1.5
        radiusOfSmallDots = coefficient / 40f
        radiusOfBigDots = coefficient / 20f

        shadowRadius = coefficient / 5f
        shadowDx = 0f
        shadowDy = coefficient / 2f
        shadowColor = Color.BLACK
    }

    override fun onDraw(canvas: Canvas) {
        if (!isInit) {
            initClock()
        }
        drawInsideCircle(canvas)
        drawCircle(canvas)
        drawCenter(canvas)
        drawNumeral(canvas)
        drawDots(canvas)
        drawHands(canvas)
        postInvalidateDelayed(500)
        invalidate()
    }

    private fun drawHand(canvas: Canvas, loc: Double, isHour: Boolean, isMinute: Boolean) {
        paint?.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)

        val angle = Math.PI * loc / 30 - Math.PI / 2
        var handRadius = radius
        if (isHour) {
            paint!!.strokeWidth = coefficient / 5f
            handRadius -= hourHandTruncation
        } else if (isMinute) {
            paint!!.strokeWidth = coefficient / 8f
            handRadius -= minuteHandTruncation
        } else {
            paint!!.strokeWidth = coefficient / 15f
        }
        canvas.drawLine(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            (width / 2 + cos(angle) * handRadius).toFloat(),
            (height / 2 + sin(angle) * handRadius).toFloat(),
            paint!!
        )

        paint?.clearShadowLayer()
    }

    private fun drawHands(canvas: Canvas) {
        val c = Calendar.getInstance()
        var hour = c[Calendar.HOUR_OF_DAY].toFloat()
        hour = if (hour > 12) hour - 12 else hour
        drawHand(canvas, ((hour + c[Calendar.MINUTE] / 60) * 5f).toDouble(), isHour = true, isMinute = false)
        drawHand(canvas, c[Calendar.MINUTE].toDouble(), isHour = false, isMinute = true)
        drawHand(canvas, c[Calendar.SECOND].toDouble(), isHour = false, isMinute = false)
    }

    private fun drawNumeral(canvas: Canvas) {
        paint!!.textSize = fontSize.toFloat()
        for (number in numbers) {
            val tmp = number.toString()
            paint!!.getTextBounds(tmp, 0, tmp.length, rect)
            val angle = Math.PI / 6 * (number - 3)
            val x = (width / 2 + cos(angle) * radius - rect.width() / 2).toInt()
            val y = (height / 2 + sin(angle) * radius + rect.height() / 2).toInt()
            canvas.drawText(tmp, x.toFloat(), y.toFloat(), paint!!)
        }
    }

    private fun drawDots(canvas: Canvas) {
        for (dotCounter in 1..60) {
            val angle = Math.PI / 30 * (dotCounter - 3)
            val x = (width / 2 + cos(angle) * radiusForDots).toInt()
            val y = (height / 2 + sin(angle) * radiusForDots).toInt()
            if (dotCounter % 5 == 3) {
                canvas.drawCircle(x.toFloat(), y.toFloat(), radiusOfBigDots, paint!!)
            } else {
                canvas.drawCircle(x.toFloat(), y.toFloat(), radiusOfSmallDots, paint!!)
            }
        }
    }

    private fun drawCenter(canvas: Canvas) {
        paint!!.style = Paint.Style.FILL
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), coefficient / 7f, paint!!)
    }

    private fun drawCircle(canvas: Canvas) {
        paint?.setShadowLayer(shadowRadius, coefficient / 4f, 0f, Color.DKGRAY)

        paint!!.color = resources.getColor(R.color.black)
        paint!!.strokeWidth = coefficient / 2.5f
        paint!!.style = Paint.Style.STROKE
        canvas.drawCircle(
            (width / 2).toFloat(), (height / 2).toFloat(), (radius + 0.9 * padding).toFloat(),
            paint!!
        )

        paint?.clearShadowLayer()
    }

    private fun drawInsideCircle(canvas: Canvas) {
        paint!!.reset()
        paint!!.color = resources.getColor(R.color.darker_gray)
        paint!!.style = Paint.Style.FILL
        paint!!.isAntiAlias = true
        canvas.drawCircle(
            (width / 2).toFloat(), (height / 2).toFloat(), (radius + 0.9 * padding).toFloat(),
            paint!!
        )
    }
}