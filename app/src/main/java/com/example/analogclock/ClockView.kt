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
import java.lang.Math.min
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin


class ClockView : View {
    private var height = 0
    private var width = 0
    private var padding = 0
    private var fontSize = 0
    private val numeralSpacing = 0
    private var handTruncation = 0
    private var hourHandTruncation = 0
    private var minuteHandTruncation = 0
    private var radius = 0
    private var paint: Paint? = null
    private var isInit = false
    private val numbers = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    private val rect = Rect()

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
        padding =
            if (fontSize < 20) numeralSpacing + (fontSize * 1.5).toInt()
            else numeralSpacing + fontSize
        radius = min / 2 - padding
        handTruncation = min / 25
        hourHandTruncation = min / 7
        minuteHandTruncation = min / 10
        paint = Paint()
        isInit = true
    }

    override fun onDraw(canvas: Canvas) {
        if (!isInit) {
            initClock()
        }
        canvas.drawColor(Color.BLACK)
        drawCircle(canvas)
        drawCenter(canvas)
        drawNumeral(canvas)



        drawHands(canvas)
        postInvalidateDelayed(500)
        invalidate()
    }

    private fun drawHand(canvas: Canvas, loc: Double, isHour: Boolean, isMinute: Boolean) {
        val angle = Math.PI * loc / 30 - Math.PI / 2
        paint!!.color = resources.getColor(R.color.holo_orange_light)
        var handRadius = radius
            if (isHour) {
                paint!!.strokeWidth = fontSize / 5f
                handRadius -= handTruncation + hourHandTruncation
            } else if (isMinute) {
                paint!!.strokeWidth = fontSize / 8f
                handRadius -= minuteHandTruncation
            } else {
                paint!!.strokeWidth = fontSize / 15f
            }
        canvas.drawLine(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            (width / 2 + cos(angle) * handRadius).toFloat(),
            (height / 2 + sin(angle) * handRadius).toFloat(),
            paint!!
        )
    }

    private fun drawHands(canvas: Canvas) {
        val c = Calendar.getInstance()
        var hour = c[Calendar.HOUR_OF_DAY].toFloat()
        hour = if (hour > 12) hour - 12 else hour
        drawHand(canvas, ((hour + c[Calendar.MINUTE] / 60) * 5f).toDouble(), true, false)
        drawHand(canvas, c[Calendar.MINUTE].toDouble(), false, true)
        drawHand(canvas, c[Calendar.SECOND].toDouble(), false, false)
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

    private fun drawCenter(canvas: Canvas) {
        paint!!.style = Paint.Style.FILL
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), fontSize / 3f, paint!!)
    }

    private fun drawCircle(canvas: Canvas) {
        paint!!.reset()
        paint!!.color = resources.getColor(R.color.holo_orange_dark)
//        paint!!.strokeWidth = 5f
        paint!!.strokeWidth = fontSize / 2f  //
        paint!!.style = Paint.Style.STROKE
        paint!!.isAntiAlias = true
        canvas.drawCircle(
            (width / 2).toFloat(), (height / 2).toFloat(), (radius + padding - 10).toFloat(),
            paint!!
        )
    }
}