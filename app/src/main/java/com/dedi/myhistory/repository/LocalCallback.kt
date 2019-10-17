package com.dedi.myhistory.repository

import androidx.paging.DataSource
import com.dedi.myhistory.data.HistoryModel

interface LocalCallback {
    fun insert(historyModel: HistoryModel)
    fun getAll(): DataSource.Factory<Int,HistoryModel>
    fun update (historyModel: HistoryModel)
    fun delete (historyModel: HistoryModel)
}