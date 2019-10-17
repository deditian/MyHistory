package com.dedi.myhistory.di

import androidx.room.Room
import com.dedi.myhistory.detail.DetailActivityViewModel
import com.dedi.myhistory.home.HomeActivityViewModel
import com.dedi.myhistory.repository.LocalCallback
import com.dedi.myhistory.repository.LocalRepository
import com.dedi.myhistory.room.MyDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext


object DependencyModules {

    val appModules = applicationContext {

        bean { LocalRepository(get()) as LocalCallback }


        factory { HomeActivityViewModel(get()) }

        factory { DetailActivityViewModel(get()) }

        bean { get<MyDatabase>().favDao() }

        bean {
            Room.databaseBuilder(androidApplication(), MyDatabase::class.java, "History-db").allowMainThreadQueries()
                .build()
        }
    }
}