package com.viktorger.fineweather.presentation.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.viktorger.fineweather.R
import kotlin.math.min

class TemperatureLineChartView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var rect = Rect()
    private val diagramPaint = Paint().apply {
        strokeWidth = dpToPx(3F)
        style = Paint.Style.STROKE
        color = Color.parseColor("#FF0080FF")
    }
    private val decorBoxPaint = Paint().apply {
        strokeWidth = dpToPx(2F)
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#FF0080FF")
    }
    private val textPaint = Paint().apply {
        textSize = spToPx(14f)
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
    }
    private val path = Path()

    private var timeList: List<String> = listOf()

    private var temperatureList: List<Int> = listOf()
        set(list) {
            field = list

            val diagramBottom = height - decorBoxHeight
            val diagramTop = dpToPx(30F)

            val maxTemp = list.max()
            val minTemp = list.min()
            val coefficient: Float = (diagramBottom - diagramTop) / (maxTemp - minTemp)

            val verticesList = mutableListOf<Pair<Float, Float>>()
            var curX: Float = 0F
            var curY: Float

            val sideMargin = width / list.size

            verticesList.add(Pair(curX, diagramBottom - (list[0] - minTemp) * coefficient))

            curX = sideMargin / 2F

            list.forEach {
                curY = diagramBottom - (it - minTemp) * coefficient
                verticesList.add(Pair(curX, curY))
                curX += sideMargin
            }

            verticesList.add(Pair(width.toFloat(), verticesList.last().second))

            vertices = verticesList
        }

    private var vertices: List<Pair<Float, Float>> = listOf()

    private var decorBoxHeight: Int = (spToPx(13F) + dpToPx(10F)).toInt()

    private var sourceList: List<String> = listOf()

    init {
        val typedArray = context?.obtainStyledAttributes(
            attrs, R.styleable.TemperatureLineChartView
        )

        typedArray?.let {
            diagramPaint.color = it.getColor(
                R.styleable.TemperatureLineChartView_overallColor, diagramPaint.color
            )

            decorBoxPaint.color = it.getColor(
                R.styleable.TemperatureLineChartView_overallColor, decorBoxPaint.color
            )

            decorBoxHeight = it
                .getDimension(
                    R.styleable.TemperatureLineChartView_decorBoxHeight,
                    decorBoxHeight.toFloat()
                ).toInt()
        }

        typedArray?.recycle()
    }

    private fun dpToPx(dp: Float) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
    )

    private fun spToPx(sp: Float): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics
    )

    fun setTempTimeSource(temperatureList: List<Int>, timeList: List<String>, sourceList: List<String>) {
        this.temperatureList = temperatureList
        this.timeList = timeList
        this.sourceList = sourceList

        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val desiredWidth = dpToPx(1200F).toInt()
        val desiredHeight = dpToPx(200F).toInt()

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredWidth, widthMeasureSpec)
            else -> desiredWidth
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredHeight, heightMeasureSpec)
            else -> desiredHeight
        }

        setMeasuredDimension(width, height)

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (vertices.isEmpty()) return

        path.rewind()

        path.moveTo(vertices[0].first, vertices[0].second)
        for (i in 1 until vertices.lastIndex) {
            path.lineTo(vertices[i].first, vertices[i].second)
            canvas.drawText(
                "${temperatureList[i - 1]}\u00B0",
                vertices[i].first,
                vertices[i].second - spToPx(14F),
                textPaint
            )

            canvas.drawText(timeList[i - 1], vertices[i].first, height.toFloat(), textPaint)
        }

        path.lineTo(vertices.last().first, vertices.last().second)

        canvas.drawPath(path, diagramPaint)
    }
}