package me.ekhaled1836.hijricalendarview.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.GridLayoutAnimationController
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import me.ekhaled1836.hijricalendarview.R
import me.ekhaled1836.hijricalendarview.util.CalendarUtils
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

// TODO: Paging the View and the Data with the Architecture components.
class CalendarView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
    private val mToday: UmmalquraCalendar = UmmalquraCalendar()
    private var mCurrentDate: UmmalquraCalendar
    private var mPrevKey: UmmalquraCalendar = mToday
    private var mNextKey: UmmalquraCalendar = mToday
    private var mRowNum: Int = 5

    private var mMonthChangedEventListener: MonthChangedEventListener = MonthChangedEventListener()

    private val hijriMonthsNames = arrayListOf(
        "محرم", "صفر", "ربيع الأول", "ربيع الثاني", "جمادى الأولى", "جمادى الآخرة",
        "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
    )

    private val gregorianMonthsNames = arrayListOf(
        "يناير", "فبراير", "مارس", "إبريل", "مايو", "يونيه",
        "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    )

    init {
        // TODO: Use the colors.
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.HijriCalendarView,
            0, 0
        ).apply {
            try {
                val dayNameTintColor = getColor(
                    R.styleable.HijriCalendarView_dayNameTintColor,
                    ResourcesCompat.getColor(resources, R.color.day_background_name_tint, context.theme)
                )
                val currentDayColor = getColor(
                    R.styleable.HijriCalendarView_currentDayColor,
                    ResourcesCompat.getColor(resources, R.color.day_background_current, context.theme)
                )
                val selectedDayColor = getColor(
                    R.styleable.HijriCalendarView_selectedDayColor,
                    ResourcesCompat.getColor(resources, R.color.day_background_selected, context.theme)
                )
                val normalHijriDayTextColor = getColor(
                    R.styleable.HijriCalendarView_normalHijriDayTextColor,
                    ResourcesCompat.getColor(resources, R.color.day_text_primary_normal, context.theme)
                )
                val normalGregorianDayTextColor = getColor(
                    R.styleable.HijriCalendarView_normalGregorianDayTextColor,
                    ResourcesCompat.getColor(resources, R.color.day_text_secondary_normal, context.theme)
                )
                val selectedHijriDayTextColor = getColor(
                    R.styleable.HijriCalendarView_selectedHijriDayTextColor,
                    ResourcesCompat.getColor(resources, R.color.day_text_primary_selected, context.theme)
                )
                val selectedGregorianDayTextColor = getColor(
                    R.styleable.HijriCalendarView_selectedGregorianDayTextColor,
                    ResourcesCompat.getColor(resources, R.color.day_text_secondary_selected, context.theme)
                )
                val currentHijriDayTextColor = getColor(
                    R.styleable.HijriCalendarView_currentHijriDayTextColor,
                    ResourcesCompat.getColor(resources, R.color.day_text_primary_current, context.theme)
                )
                val currentGregorianDayTextColor = getColor(
                    R.styleable.HijriCalendarView_currentGregorianDayTextColor,
                    ResourcesCompat.getColor(resources, R.color.day_text_secondary_current, context.theme)
                )
            } finally {
                recycle()
            }
        }

        mToday.set(Calendar.DAY_OF_MONTH, 1)
        mCurrentDate = mToday

        val listing = CalendarUtils.getMonth(mToday, Calendar.SATURDAY, true)

        mRowNum = if (listing.list.size <= 35) 5 else 6
        mPrevKey = listing.prev
        mNextKey = listing.next

        adapter = CalendarAdapter(
            mCurrentDate.get(Calendar.YEAR),
            mCurrentDate.get(Calendar.MONTH),
            listing.list,
            listing.currentDateIndex
        )

        /* If the data set changes -> scheduleLayoutAnimation().
         * If it doesn't -> invalidate().
         * Can be set from XML.
         */
        layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.grid_slide_from_bottom)

        /* RecyclerView size changes every time you add something no matter what.
         * What setHasFixedSize does is that it makes sure (by user input) that this change of size of RecyclerView is constant.
         * The height (or width) of the item won't change. Every item added or removed will be the same.
         * If you dont set this it will check if the size of the item has changed and thats expensive.
         */
        setHasFixedSize(true)

        /* Hides the end of scroll indicator. */
        overScrollMode = View.OVER_SCROLL_NEVER

        /* Layouts from the right. */
        layoutDirection = View.LAYOUT_DIRECTION_RTL

        layoutManager = GridLayoutManager(context, 7)
    }

    fun nextMonth() {
        val listing = CalendarUtils.getMonth(
            mNextKey,
            Calendar.SATURDAY,
            mNextKey.get(Calendar.MONTH) == mToday.get(Calendar.MONTH) &&
                    mNextKey.get(Calendar.YEAR) == mToday.get(Calendar.YEAR)
        )

        val newRowNum = if (listing.list.size <= 35) 5 else 6
        if (newRowNum != mRowNum) requestLayout()
        mRowNum = newRowNum
        mCurrentDate = mNextKey
        mPrevKey = listing.prev
        mNextKey = listing.next

        (adapter as CalendarAdapter).changeDate(
            mCurrentDate.get(Calendar.YEAR),
            mCurrentDate.get(Calendar.MONTH),
            listing.list,
            listing.currentDateIndex
        )
        mMonthChangedEventListener.onMonthChanged?.invoke(getMonthName())
    }

    fun previousMonth() {
        val listing = CalendarUtils.getMonth(
            mPrevKey,
            Calendar.SATURDAY,
            mPrevKey.get(Calendar.MONTH) == mToday.get(Calendar.MONTH) &&
                    mPrevKey.get(Calendar.YEAR) == mToday.get(Calendar.YEAR)
        )

        // TODO: requestLayout?
        val newRowNum = if (listing.list.size <= 35) 5 else 6
        if (newRowNum != mRowNum) requestLayout()
        mRowNum = newRowNum
        mCurrentDate = mPrevKey
        mPrevKey = listing.prev
        mNextKey = listing.next

        (adapter as CalendarAdapter).changeDate(
            mCurrentDate.get(Calendar.YEAR),
            mCurrentDate.get(Calendar.MONTH),
            listing.list,
            listing.currentDateIndex
        )
        mMonthChangedEventListener.onMonthChanged?.invoke(getMonthName())
    }

    private fun getMonthName(): String {
        val primaryCalendar = UmmalquraCalendar()
        primaryCalendar.time = mCurrentDate.time
        val gregorianCalendar = GregorianCalendar()
        gregorianCalendar.time = primaryCalendar.time
        val startMonth = gregorianCalendar.get(Calendar.MONTH)
        primaryCalendar.set(Calendar.DAY_OF_MONTH, primaryCalendar.lengthOfMonth())
        gregorianCalendar.time = primaryCalendar.time
        val endMonth = gregorianCalendar.get(Calendar.MONTH)
        val numberFormatter = NumberFormat.getInstance(Locale("ar"))
        numberFormatter.isGroupingUsed = false
        return "${hijriMonthsNames[mCurrentDate.get(Calendar.MONTH)]} - ${numberFormatter.format(
            mCurrentDate.get(
                Calendar.YEAR
            )
        )}\n" +
                "${gregorianMonthsNames[startMonth]}${if (startMonth != endMonth) " - ${gregorianMonthsNames[endMonth]}" else ""}"
    }

    // There are three other ways to animate.
    // 1. onBindViewHolder
    // 2. ItemAnimator
    // 3. A hacky messy way using layout rendering Tree
    override fun attachLayoutAnimationParameters(
        child: View, params: ViewGroup.LayoutParams,
        index: Int, count: Int
    ) {
        val layoutManager = layoutManager
        if (adapter != null && layoutManager is GridLayoutManager) {

            var animationParams: GridLayoutAnimationController.AnimationParameters? =
                params.layoutAnimationParameters as GridLayoutAnimationController.AnimationParameters?

            if (animationParams == null) {
                // If there are no animation parameters, create new once and attach them to
                // the LayoutParams.
                animationParams = GridLayoutAnimationController.AnimationParameters()
                params.layoutAnimationParameters = animationParams
            }

            // Next we are updating the parameters

            // Set the number of items in the RecyclerView and the index of this item
            animationParams.count = count
            animationParams.index = index

            // Calculate the number of columns and rows in the grid
            val columns = layoutManager.spanCount
            animationParams.columnsCount = columns
            animationParams.rowsCount = count / columns

            // Calculate the column/row position in the grid
            val invertedIndex = count - 1 - index
            animationParams.column = columns - 1 - invertedIndex % columns
            animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns

        } else {
            // Proceed as normal if using another type of LayoutManager
            super.attachLayoutAnimationParameters(child, params, index, count)
        }
    }

    /*fun setOnMonthChangedEventListener(monthChangedEventListener: MonthChangedEventListener) {
        mMonthChangedEventListener = monthChangedEventListener
         mMonthChangedEventListener?.onMonthChanged(getMonthName())
    }*/

    fun setOnMonthChangedEventListener(monthChangedEventListener: (monthName: String) -> Unit) {
        mMonthChangedEventListener.onMonthChanged = monthChangedEventListener
        mMonthChangedEventListener.onMonthChanged?.invoke(getMonthName())
    }

    fun setOnDayPickedEventListener(datePickedEventListener: DatePickedEventListener) =
        (adapter as CalendarAdapter?)?.setDatePickedEventListener(datePickedEventListener)

    class MonthChangedEventListener {
        /* fun onMonthChanged(monthName: String) */
        var onMonthChanged: ((monthName: String) -> Unit)? = null
    }

    interface DatePickedEventListener {
        fun onDatePicked(year: Int, month: Int, day: Int)
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        /* var width = View.resolveSize(1080, widthSpec)
        var height = View.resolveSize(1080, heightSpec)
        if (width <= height) {
            height = ceil(width.toDouble() / 7.0 * mRowNum.toDouble()).toInt()
            height += (mRowNum - (height % mRowNum))
            width -= (width % 7)
        } else {
            width = ceil(height.toDouble() / mRowNum.toDouble() * 7.0).toInt()
            width += (7 - (width % 7))
            height -= (height % mRowNum)
        } */

        var width = 0
        var height = 0

        val widthMode = View.MeasureSpec.getMode(widthSpec)
        var widthSize = View.MeasureSpec.getSize(widthSpec)
        val heightMode = View.MeasureSpec.getMode(heightSpec)
        var heightSize = View.MeasureSpec.getSize(heightSpec)

        when (heightMode) {
            MeasureSpec.EXACTLY -> {
                when (widthMode) {
                    MeasureSpec.EXACTLY -> {
                        width = widthSize
                    }
                    MeasureSpec.AT_MOST -> {
                        var desiredWidth = (heightSize * 7.0 / mRowNum).roundToInt()
                        desiredWidth += 7 - (desiredWidth % 7)
                        if (widthSize >= desiredWidth) {
                            width = desiredWidth
                        } else {
                            widthSize -= (widthSize % 7)
                            width = widthSize
                        }
                    }
                    MeasureSpec.UNSPECIFIED -> {
                        var desiredWidth = (heightSize * 7.0 / mRowNum).roundToInt()
                        desiredWidth += 7 - (desiredWidth % 7)
                        width = desiredWidth
                    }
                }
                height = heightSize
            }
            MeasureSpec.AT_MOST -> {
                when (widthMode) {
                    MeasureSpec.EXACTLY -> {
                        var desiredHeight = (widthSize / 7.0 * mRowNum).roundToInt()
                        desiredHeight += mRowNum - (desiredHeight % mRowNum)
                        if (heightSize >= desiredHeight) {
                            height = desiredHeight
                        } else {
                            heightSize -= (heightSize % 7)
                            height = heightSize
                        }
                        width = widthSize
                    }
                    MeasureSpec.AT_MOST -> {
                        var desiredWidth = (heightSize * 7.0 / mRowNum).roundToInt()
                        desiredWidth += 7 - (desiredWidth % 7)
                        var desiredHeight = (widthSize / 7.0 * mRowNum).roundToInt()
                        desiredHeight += mRowNum - (desiredHeight % mRowNum)
                        if (desiredHeight <= heightSize) {
                            if (desiredWidth <= widthSize) {
                                if (desiredWidth >= widthSize) {
                                    width = desiredWidth
                                    height = heightSize
                                } else {
                                    width = widthSize
                                    height = desiredHeight
                                }
                            } else {
                                width = widthSize
                                height = desiredHeight
                            }
                        } else {
                            if (desiredWidth <= widthSize) {
                                width = desiredWidth
                                height = heightSize
                            } else {
                                width = widthSize
                                height = desiredHeight
                            }
                        }
                    }
                    MeasureSpec.UNSPECIFIED -> {
                        var desiredWidth = (heightSize * 7.0 / mRowNum).roundToInt()
                        desiredWidth += 7 - (desiredWidth % 7)
                        width = desiredWidth
                        height = heightSize
                    }
                }
            }
            MeasureSpec.UNSPECIFIED -> {
                when (widthMode) {
                    MeasureSpec.EXACTLY -> {
                        var desiredHeight = (widthSize / 7.0 * mRowNum).roundToInt()
                        desiredHeight += mRowNum - (desiredHeight % mRowNum)
                        width = widthSize
                        height = desiredHeight
                    }
                    MeasureSpec.AT_MOST -> {
                        var desiredHeight = (widthSize / 7.0 * mRowNum).roundToInt()
                        desiredHeight += mRowNum - (desiredHeight % mRowNum)
                        width = widthSize
                        height = desiredHeight
                    }
                    MeasureSpec.UNSPECIFIED -> {
                        var desiredWidth = (224 * context.resources.displayMetrics.density).roundToInt()
                        desiredWidth += 7 - (desiredWidth % 7)
                        var desiredHeight = (desiredWidth / 7.0 * mRowNum).roundToInt()
                        desiredHeight += mRowNum - (desiredHeight % mRowNum)
                        width = desiredWidth
                        height = desiredHeight
                    }
                }
            }
        }

        setMeasuredDimension(width, height)
    }

/* class SlideUpItemAnimator: DefaultItemAnimator() {
    override fun animateChange(
        oldHolder: ViewHolder?,
        newHolder: ViewHolder?,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean {
        return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY)
    }
} */

/* minimumWidth = (224 * context.resources.displayMetrics.density).toInt()
minimumHeight = (224 * context.resources.displayMetrics.density).toInt()
val desiredWidth = if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1)
    suggestedMinimumWidth + paddingStart + paddingEnd
else
    suggestedMinimumWidth + paddingLeft + paddingRight
val desiredHeight = if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1)
    suggestedMinimumHeight + paddingStart + paddingEnd
else
    suggestedMinimumHeight + paddingLeft + paddingRight */

// Does what View.resolveSize() does.
/*private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
    val specMode = View.MeasureSpec.getMode(measureSpec)
    val specSize = View.MeasureSpec.getSize(measureSpec)

    return when(specMode) {
        MeasureSpec.EXACTLY -> specSize
        MeasureSpec.AT_MOST -> min(desiredSize, specSize)
        MeasureSpec.UNSPECIFIED -> desiredSize
        else -> desiredSize
    }
}*/


/* val snapHelper = object : SnapHelper() {
    val mVerticalHelper by lazy {
        OrientationHelper.createVerticalHelper(layoutManager)
    }

    override fun findSnapView(layoutManager: LayoutManager?): View? {
        if((layoutManager?.childCount ?: 0) == 0) return null
        for(i in 0 .. (layoutManager!!.childCount - 1) step 7) {
            val child = layoutManager.getChildAt(i) as AppCompatTextView?
            val text = child?.text
            if(text.isNullOrBlank() || text.startsWith("1\n")) {
                return child
            }
        }
        return null
    }

    override fun findSnapView(layoutManager: LayoutManager?): View? {
        if ((layoutManager?.childCount ?: 0) == 0) return null
        for (i in 0..(layoutManager!!.childCount - 1) step 7) {
            val child = layoutManager.getChildAt(i) as AppCompatTextView?
            val text = child?.text
            if (text.isNullOrBlank() || text.startsWith("1\n")) {
                val firstPos: Int = i / 7
                return if (firstPos < 3) {
                    layoutManager.getChildAt(17 + firstPos * 7)
                } else {
                    layoutManager.getChildAt(3 + firstPos * 7)
                }
            }
        }
        return null
    }

    override fun calculateDistanceToFinalSnap(layoutManager: LayoutManager, targetView: View): IntArray? =
        intArrayOf(0, mVerticalHelper.getDecoratedStart(targetView))
} */


/* override fun onScrollStateChanged(state: Int) {
    super.onScrollStateChanged(state)

    if(state == RecyclerView.SCROLL_STATE_IDLE) {
        val layoutManager = this.layoutManager
        if(layoutManager != null) {
            val text = (layoutManager.getChildAt(layoutManager.childCount - 1) as AppCompatTextView).text
            if(text.isNullOrBlank()) {

            }
        }
    }
} */

    /* override fun fling(velocityX: Int, velocityY: Int): Boolean = false */
}
