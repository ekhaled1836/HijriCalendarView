package me.ekhaled1836.hijricalendarview.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import java.util.concurrent.Executor

class CalendarRepository(
    private val dataSourceFactory: CalendarDataSource.CalendarDataSourceFactory,
    private val loadingExecutor: Executor
) {
    fun getCalendarLiveData(): LiveData<PagedList<Pair<Int, Int>?>> {
        val pagedListConfig = PagedList.Config.Builder().apply {
            setEnablePlaceholders(false)
            setPrefetchDistance(168)
        }.build()
        return dataSourceFactory.toLiveData(pagedListConfig, null, null, loadingExecutor)
    }
}