package com.dedi.myhistory.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dedi.myhistory.data.AddressModel
import com.dedi.myhistory.model.HistoryModel
import com.dedi.myhistory.repository.LocalCallback
import com.dedi.myhistory.repository.RemoteCallback

class HomeActivityViewModel(val localCallback: LocalCallback, val remoteCallback: RemoteCallback):ViewModel(){

    fun saveHis(historyModel: HistoryModel) {
        localCallback.insert(historyModel)
    }

    fun getAllHistory(): LiveData<PagedList<HistoryModel>> {
        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(20).build()
        return LivePagedListBuilder(localCallback.getAll(), pagedListConfig).build()
    }

    fun getAddress(prox:String, mode:String, maxresults: Int, gen:Int,app_id:String,app_code:String): LiveData<AddressModel> {
        return remoteCallback.getAddress(prox, mode, maxresults, gen,app_id,app_code)
    }
}