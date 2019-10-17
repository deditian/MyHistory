package com.dedi.myhistory.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ParceModel(
        val id : Long,
        val title : String,
        val date: String,
        val location : String,
        val description : String
):Parcelable