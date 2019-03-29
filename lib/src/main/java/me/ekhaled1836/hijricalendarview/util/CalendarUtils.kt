package me.ekhaled1836.hijricalendarview.util

import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import me.ekhaled1836.hijricalendarview.model.MonthListing
import java.util.*
import kotlin.collections.ArrayList

class CalendarUtils {
    companion object {
        // TODO: There will be a time when three Gregorian months will parallel one Hijri month.
        fun getMonth(
            ummalquraCalendar: UmmalquraCalendar,
            firstDayOfWeek: Int,
            currentMonth: Boolean
        ): MonthListing {
            val primaryDaysList = ArrayList<Int?>(42)
            val secondaryDaysList = ArrayList<Int?>(42)

            val primaryCalendar = UmmalquraCalendar()
            if (!currentMonth) primaryCalendar.time = ummalquraCalendar.time

            val primaryDayOfMonth = primaryCalendar.get(Calendar.DAY_OF_MONTH)
            if (currentMonth) primaryCalendar.set(Calendar.DAY_OF_MONTH, 1)
            val primaryDayOfWeek = primaryCalendar.get(Calendar.DAY_OF_WEEK)
            var firstDayOfMonthIndex = (primaryDayOfWeek - firstDayOfWeek) % 7
            if (firstDayOfMonthIndex < 0) firstDayOfMonthIndex += 7
            val primaryDayOfMonthIndex = firstDayOfMonthIndex + primaryDayOfMonth - 1
            val primaryMonthLength = primaryCalendar.lengthOfMonth()

            for (i in 0 until firstDayOfMonthIndex) {
                primaryDaysList.add(null)
                secondaryDaysList.add(null)
            }

            for (i in 1..primaryMonthLength) primaryDaysList.add(i)

            val secondaryCalendar = GregorianCalendar()
            secondaryCalendar.time = primaryCalendar.time
            val secondaryDayOfMonth = secondaryCalendar[Calendar.DAY_OF_MONTH]
            val secondaryMonthLength = secondaryCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            for (i in secondaryDayOfMonth..secondaryMonthLength) secondaryDaysList.add(i)
            for (i in 1..primaryMonthLength - (secondaryMonthLength - secondaryDayOfMonth)) {
                secondaryDaysList.add(i)
            }

            val list = ArrayList<Pair<Int, Int>?>(42)
            for (i in 0 until primaryDaysList.size) {
                list.add(
                    if (primaryDaysList[i] == null) null
                    else primaryDaysList[i]!! to secondaryDaysList[i]!!
                )
            }

            primaryCalendar.set(Calendar.MONTH, primaryCalendar.get(Calendar.MONTH) - 1)
            val prev = UmmalquraCalendar()
            prev.time = primaryCalendar.time

            primaryCalendar.set(Calendar.MONTH, primaryCalendar.get(Calendar.MONTH) + 2)
            val next = UmmalquraCalendar()
            next.time = primaryCalendar.time

            return MonthListing(
                list,
                prev,
                next,
                if (currentMonth) primaryDayOfMonthIndex else -1
            )
        }
    }
}