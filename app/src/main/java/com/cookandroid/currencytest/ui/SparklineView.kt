package com.cookandroid.currencytest.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.cookandroid.currencytest.R

class SparklineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    private var points: List<Float> = emptyList()
    private var positiveTrend: Boolean = true
    private var riseColorRes: Int = R.color.riseGreen
    private var fallColorRes: Int = R.color.fallBlue

    fun setData(
        data: List<Float>,
        isPositiveTrend: Boolean,
        riseColor: Int = R.color.riseGreen,
        fallColor: Int = R.color.fallBlue
    ) {
        points = data
        positiveTrend = isPositiveTrend
        riseColorRes = riseColor
        fallColorRes = fallColor
        applyColors()
        invalidate()
    }

    private fun applyColors() {
        val color = ContextCompat.getColor(context, if (positiveTrend) riseColorRes else fallColorRes)
        linePaint.color = color
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (points.size < 2) return

        val width = width.toFloat()
        val height = height.toFloat()
        val min = points.minOrNull() ?: 0f
        val max = points.maxOrNull() ?: 0f
        val range = (max - min).coerceAtLeast(1f)
        val stepX = width / (points.size - 1)

        val linePath = Path()

        points.forEachIndexed { index, value ->
            val x = index * stepX
            val normalized = (value - min) / range
            val y = height - (normalized * height)
            if (index == 0) {
                linePath.moveTo(x, y)
            } else {
                linePath.lineTo(x, y)
            }
        }
        canvas.drawPath(linePath, linePaint)
    }
}
