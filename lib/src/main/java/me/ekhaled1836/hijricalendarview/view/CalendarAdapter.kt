package me.ekhaled1836.hijricalendarview.view

import android.graphics.Typeface.BOLD
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import me.ekhaled1836.hijricalendarview.R
import java.text.NumberFormat
import java.util.*
import kotlin.math.min

class CalendarAdapter(
    private var mYear: Int,
    private var mMonth: Int,
    private var mList: ArrayList<Pair<Int, Int>?>,
    private var mCurrentDateIndex: Int
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mDatePickedEventListener: CalendarView.DatePickedEventListener? = null
    private var mSelectedDateIndex: Int = -1

    private var mNullEnd: Int = -1

    private lateinit var mRecyclerView: RecyclerView

    private val numberFormatter by lazy {
        NumberFormat.getInstance(Locale("ar", "EG"))
    }

    init {
        for (i in 0 until 7) {
            if (mList[i] != null) {
                mNullEnd = i; break
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val textView = holder.itemView as AppCompatTextView
        val newDate = mList[position]

        if (newDate != null) {
            textView.visibility = View.VISIBLE

            val spannableString = SpannableString(
                numberFormatter.format(newDate.first) +
                        "\n" +
                        numberFormatter.format(newDate.second)
            )
            val primaryDateLength = if (spannableString[1] == '\n') 1 else 2

            spannableString.setSpan(
                StyleSpan(BOLD),
                0,
                primaryDateLength,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // setBackgroundResource Checks if it's the old one first.
            // Removing background, because of overdraw. It worked!!
            if (position == mCurrentDateIndex) {
                textView.setBackgroundResource(R.drawable.day_current)
                changeText(
                    spannableString,
                    textView,
                    primaryDateLength,
                    R.color.day_text_primary_current,
                    R.color.day_text_secondary_current
                )
            } else {
                textView.setBackgroundResource(0)
                changeText(
                    spannableString,
                    textView,
                    primaryDateLength,
                    R.color.day_text_primary_normal,
                    R.color.day_text_secondary_normal
                )
            }

            textView.setOnClickListener {
                if (position != mSelectedDateIndex) {
                    if (mSelectedDateIndex != mCurrentDateIndex) {
                        val oldTextView =
                            mRecyclerView.findViewHolderForAdapterPosition(mSelectedDateIndex)?.itemView as AppCompatTextView?

                        if (oldTextView != null) {
                            oldTextView.setBackgroundResource(0)

                            val oldSpannableString = SpannableString(
                                String.format(Locale("ar", "EG"), "${oldTextView.text}")
                            )
                            val oldPrimaryDateLength = if (oldSpannableString[1] == '\n') 1 else 2

                            oldSpannableString.setSpan(
                                StyleSpan(BOLD),
                                0,
                                oldPrimaryDateLength,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )

                            changeText(
                                oldSpannableString,
                                oldTextView,
                                oldPrimaryDateLength,
                                R.color.day_text_primary_normal,
                                R.color.day_text_secondary_normal
                            )
                        }
                    }

                    if (position != mCurrentDateIndex) {
                        textView.setBackgroundResource(R.drawable.day_selected)

                        changeText(
                            spannableString,
                            textView,
                            primaryDateLength,
                            R.color.day_text_primary_selected,
                            R.color.day_text_secondary_selected
                        )
                    }

                    mSelectedDateIndex = position
                    mDatePickedEventListener?.onDatePicked(mYear, mMonth, newDate.first)
                }
            }
        } else {
            textView.setOnClickListener(null)
            textView.visibility = View.GONE
        }
    }

    private fun changeText(
        spannableString: SpannableString,
        textView: AppCompatTextView,
        primaryDateLength: Int,
        primaryDateColor: Int,
        secondaryDateColor: Int
    ) {
        spannableString.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    textView.context,
                    primaryDateColor
                )
            ),
            0,
            primaryDateLength,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    textView.context,
                    secondaryDateColor
                )
            ),
            primaryDateLength + 1,
            spannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                spannableString,
                TextViewCompat.getTextMetricsParams(textView), null
            )
        )
    }

    fun setDatePickedEventListener(datePickedEventListener: CalendarView.DatePickedEventListener) {
        mDatePickedEventListener = datePickedEventListener
        if (mSelectedDateIndex != -1) {
            datePickedEventListener.onDatePicked(
                mYear,
                mMonth,
                mList[mSelectedDateIndex]!!.first
            )
        } else if (mCurrentDateIndex != -1) {
            datePickedEventListener.onDatePicked(mYear, mMonth, mList[mCurrentDateIndex]!!.first)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun getItemCount(): Int = mList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        DateViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_day,
                parent,
                false
            )
        )

    fun changeDate(
        newYear: Int,
        newMonth: Int,
        newList: ArrayList<Pair<Int, Int>?>,
        newCurrentDateIndex: Int
    ) {
        var newNullEnd = -1
        for (i in 0 until 7) {
            if (newList[i] != null) {
                newNullEnd = i; break
            }
        }

        // A more concise, but verbose, DiffUtil.
        // In general, it calls notifyItem for us.
        // Which is more light and uses ItemAnimator.
        val changeStartPosition = min(newNullEnd, mNullEnd)
        notifyItemRangeChanged(changeStartPosition, mList.size - changeStartPosition)
        notifyItemRangeRemoved(newList.size, mList.size - newList.size)
        notifyItemRangeInserted(mList.size, newList.size - mList.size)

        mNullEnd = newNullEnd
        mYear = newYear
        mMonth = newMonth
        mList = newList
        mCurrentDateIndex = newCurrentDateIndex
        mSelectedDateIndex = -1
    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
