package com.dedi.myhistory.repository

import androidx.lifecycle.LiveData
import com.dedi.myhistory.data.AddressModel

interface RemoteCallback {
    fun getAddress(prox:String, mode:String, maxresults: Int, gen:Int,app_id:String,app_code:String): LiveData<AddressModel>
}