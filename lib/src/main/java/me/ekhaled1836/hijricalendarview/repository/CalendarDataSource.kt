package me.ekhaled1836.hijricalendarview.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import me.ekhaled1836.hijricalendarview.util.CalendarUtils
import java.util.*

class CalendarDataSource :
    PageKeyedDataSource<UmmalquraCalendar, Pair<Int, Int>?>() {
    override fun loadInitial(
        params: LoadInitialParams<UmmalquraCalendar>,
        callback: LoadInitialCallback<UmmalquraCalendar, Pair<Int, Int>?>
    ) {
        val listing = CalendarUtils.getMonth(UmmalquraCalendar(), Calendar.SATURDAY, true)
        callback.onResult(listing.list, listing.prev, listing.next)
    }

    override fun loadAfter(
        params: LoadParams<UmmalquraCalendar>,
        callback: LoadCallback<UmmalquraCalendar, Pair<Int, Int>?>
    ) {
        val listing = CalendarUtils.getMonth(params.key, Calendar.SATURDAY, false)
        callback.onResult(listing.list, listing.next)
    }

    override fun loadBefore(
        params: LoadParams<UmmalquraCalendar>,
        callback: LoadCallback<UmmalquraCalendar, Pair<Int, Int>?>
    ) {
        val listing = CalendarUtils.getMonth(params.key, Calendar.SATURDAY, false)
        callback.onResult(listing.list, listing.prev)
    }

    class CalendarDataSourceFactory :
        DataSource.Factory<UmmalquraCalendar, Pair<Int, Int>?>() {
        private val sourceLiveData = MutableLiveData<CalendarDataSource>()
        override fun create(): DataSource<UmmalquraCalendar, Pair<Int, Int>?> {
            val source = CalendarDataSource()
            sourceLiveData.postValue(source)
            return source
        }
    }
}
