package com.dedi.myhistory.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dedi.myhistory.data.AddressModel
import com.dedi.myhistory.repository.RemoteCallback


class MapViewModel(val moviesCallback: RemoteCallback) : ViewModel() {
    fun getAddress(prox:String, mode:String, maxresults: Int, gen:Int,app_id:String,app_code:String): LiveData<AddressModel> {
        return moviesCallback.getAddress(prox, mode, maxresults, gen,app_id,app_code)
    }

}