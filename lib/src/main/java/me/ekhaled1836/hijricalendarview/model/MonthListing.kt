package me.ekhaled1836.hijricalendarview.model

import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar

data class MonthListing(
    val list: ArrayList<Pair<Int, Int>?>,
    val prev: UmmalquraCalendar,
    val next: UmmalquraCalendar,
    val currentDateIndex: Int
)