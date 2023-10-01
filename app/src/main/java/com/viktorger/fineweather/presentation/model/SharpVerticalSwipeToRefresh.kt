package com.viktorger.fineweather.presentation.model

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.abs


class SharpVerticalSwipeToRefresh(context: Context, attrs: AttributeSet)
    : SwipeRefreshLayout(context, attrs) {

    private var touchSlop = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        10f,
        resources.displayMetrics
    )
    private var prevX = 0f

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                prevX = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                val eventX = event.x
                val xDiff = abs(eventX - prevX)
                if (xDiff > touchSlop) {
                    return false
                }
            }
        }
        return super.onInterceptTouchEvent(event)
    }

}