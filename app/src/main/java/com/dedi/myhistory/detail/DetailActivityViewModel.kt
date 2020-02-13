package com.dedi.myhistory.detail

import androidx.lifecycle.ViewModel
import com.dedi.myhistory.model.HistoryModel
import com.dedi.myhistory.repository.LocalCallback

class DetailActivityViewModel(val localCallback: LocalCallback) : ViewModel(){
    fun update(historyModel: HistoryModel) {
        localCallback.update(historyModel)
    }

    fun delete(historyModel: HistoryModel) {
        localCallback.delete(historyModel)
    }
}