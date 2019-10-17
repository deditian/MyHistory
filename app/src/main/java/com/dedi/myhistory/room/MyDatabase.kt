package com.dedi.myhistory.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dedi.myapplication.room.HisDao
import com.dedi.myhistory.data.HistoryModel


@Database(entities = [HistoryModel::class], version = 1)
abstract class MyDatabase: RoomDatabase() {
    abstract fun favDao(): HisDao
}