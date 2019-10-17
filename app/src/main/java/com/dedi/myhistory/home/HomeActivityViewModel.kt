package com.dedi.myhistory.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dedi.myhistory.data.HistoryModel
import com.dedi.myhistory.repository.LocalCallback

class HomeActivityViewModel(val localCallback: LocalCallback):ViewModel(){

    fun saveHis(historyModel: HistoryModel) {
        localCallback.insert(historyModel)
    }

    fun getAllHistory(): LiveData<PagedList<HistoryModel>> {
        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(20).build()
        return LivePagedListBuilder(localCallback.getAll(), pagedListConfig).build()
    }
}