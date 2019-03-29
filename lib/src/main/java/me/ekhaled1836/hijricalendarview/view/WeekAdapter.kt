package me.ekhaled1836.hijricalendarview.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import me.ekhaled1836.hijricalendarview.R

class WeekAdapter : RecyclerView.Adapter<WeekAdapter.WeekViewHolder>() {
    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        val textView = holder.itemView as AppCompatTextView
        if (position and 1 == 1) {
            textView.setBackgroundResource(R.color.day_background_name_tint)
        } else {
            textView.setBackgroundResource(0)
        }

        textView.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                dayNames[position],
                TextViewCompat.getTextMetricsParams(textView), null
            )
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder =
        WeekViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_day_name,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = 7

    class WeekViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private val dayNames = arrayListOf("السبت", "الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة")
    }
}