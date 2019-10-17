package com.dedi.myhistory.repository

import androidx.paging.DataSource
import com.dedi.myapplication.room.HisDao
import com.dedi.myhistory.data.HistoryModel

class LocalRepository(private val hisDao: HisDao):LocalCallback{
    override fun insert(historyModel: HistoryModel) {
       return hisDao.insert(historyModel)
    }

    override fun getAll(): DataSource.Factory<Int, HistoryModel> {
        return hisDao.getAllHistory()
    }

    override fun update(historyModel: HistoryModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(historyModel: HistoryModel) {
       return hisDao.delete(historyModel)
    }

}