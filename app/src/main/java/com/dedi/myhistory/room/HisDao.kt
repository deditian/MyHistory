package com.dedi.myapplication.room

import androidx.paging.DataSource
import androidx.room.*
import com.dedi.myhistory.data.HistoryModel

@Dao
interface HisDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(favModel: HistoryModel)

    @Query("SELECT * from history_table   ORDER BY date DESC")
    fun getAllHistory(): DataSource.Factory<Int, HistoryModel>

    @Delete
    fun delete(favModel: HistoryModel)

    @Update
    fun update(favModel: HistoryModel)

    @Query("DELETE FROM history_table")
    fun deleteAll()

}