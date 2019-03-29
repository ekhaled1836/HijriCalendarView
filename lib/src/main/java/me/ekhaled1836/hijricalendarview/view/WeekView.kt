package me.ekhaled1836.hijricalendarview.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WeekView(context: Context, attrs: AttributeSet? = null) : RecyclerView(context, attrs) {
    init {
        setHasFixedSize(true)
        layoutDirection = View.LAYOUT_DIRECTION_RTL
        overScrollMode = View.OVER_SCROLL_NEVER
        adapter = WeekAdapter()
        layoutManager = GridLayoutManager(context, 7)
    }
}
