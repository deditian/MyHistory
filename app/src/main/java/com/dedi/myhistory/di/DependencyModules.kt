package com.dedi.myhistory.di

import androidx.room.Room
import com.dedi.myhistory.detail.DetailActivityViewModel
import com.dedi.myhistory.ui.home.HomeActivityViewModel
import com.dedi.myhistory.repository.LocalCallback
import com.dedi.myhistory.repository.LocalRepository
import com.dedi.myhistory.repository.RemoteCallback
import com.dedi.myhistory.repository.RemoteRepository
import com.dedi.myhistory.room.MyDatabase
import com.dedi.myhistory.ui.map.MapViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext


object DependencyModules {

    val appModules = applicationContext {

        bean { LocalRepository(get()) as LocalCallback }
        bean { RemoteRepository() as RemoteCallback }


        factory { HomeActivityViewModel(get()) }
        factory { DetailActivityViewModel(get()) }
        factory { MapViewModel(get()) }

        bean { get<MyDatabase>().favDao() }

        bean {
            Room.databaseBuilder(androidApplication(), MyDatabase::class.java, "History-db").allowMainThreadQueries()
                .build()
        }
    }
}