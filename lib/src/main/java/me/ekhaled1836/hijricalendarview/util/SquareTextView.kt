package me.ekhaled1836.hijricalendarview.util

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class SquareTextView : AppCompatTextView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        /*val specSize = View.MeasureSpec.getSize(widthSpec)
        val margin = specSize / 8
        if (layoutParams is ViewGroup.MarginLayoutParams)
            (layoutParams as ViewGroup.MarginLayoutParams).setMargins(margin, margin, margin, margin)*/

        super.onMeasure(widthSpec, widthSpec)
    }
}