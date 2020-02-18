package com.dedi.myhistory.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import android.widget.Toast
import android.R
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.core.location.LocationEngineCallback
import android.content.DialogInterface
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import android.location.LocationManager
import android.content.Context.LOCATION_SERVICE
import androidx.core.content.ContextCompat.getSystemService

@Parcelize
data class ParceModel(
        val id : Long,
        val title : String,
        val date: String,
        val location : String,
        val description : String
):Parcelable


