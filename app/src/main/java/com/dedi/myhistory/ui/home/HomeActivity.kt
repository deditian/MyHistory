package com.dedi.myhistory.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity;
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dedi.myhistory.R
import com.dedi.myhistory.model.HistoryModel
import com.dedi.myhistory.ui.map.MapLocation
import com.dedi.myhistory.util.Utility
import com.google.android.material.bottomsheet.BottomSheetBehavior
import es.dmoral.toasty.Toasty

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_map_location.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.content_main.*
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    var locationManager:LocationManager?=null

    private var homeActivityAdapter: HomeActivityAdapter? = null
    val viewModel:HomeActivityViewModel by inject()

    val TAG = "HomeActivity"

    private val historyModel : ArrayList<HistoryModel> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        observeViewModelRequest()

        // Create persistent LocationManager reference
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        try {
            // Request location updates
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } catch(ex: SecurityException) {
            Log.d(TAG, "Security Exception, no location available")
        }


        homeActivityAdapter = HomeActivityAdapter(this)

        rv_history?.layoutManager = LinearLayoutManager(this)
        rv_history?.setHasFixedSize(true)
        rv_history?.adapter = homeActivityAdapter

        var sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(p0: View, p1: Float) {

            }
        })

        bottom_sheet.setOnClickListener {
            if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                val title = edt_bs_title.text.toString()
                val currentDate = SimpleDateFormat("dd/M/yyyy hh:mm").format(Date())
                val location = txt_bs_location.text.toString()
                val description = edt_bs_description.text.toString()
                var latlong = Utility(this).getValueString("label_address").toString()
                txt_bs_location.text = latlong


                txt_bs_location.setOnClickListener {

                }
                btn_bs_add.setOnClickListener {
                    Log.i("his","hasil : "+edt_bs_title.text)
                    viewModel.saveHis(
                        HistoryModel(
                            title,
                            currentDate,
                            location,
                            "",
                            "",
                            description
                        )
                    )
                    observeViewModelRequest()
                    Toasty.success(this,"Success Add Data",Toasty.LENGTH_LONG).show()
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                }

                img_location.setOnClickListener {
                    val mIntent = Intent(this, MapLocation::class.java)
                    startActivity(mIntent)
                }

            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)

            }
        }


    }

    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            txt_bs_location.text = ("deditian " + location.longitude + ":" + location.latitude)
            Log.i(TAG,  "deditian location ${location.longitude} ${location.latitude}")

        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }


    private fun observeViewModelRequest() {
        viewModel.getAllHistory().observe(this, Observer {data ->
            if (!data.isNullOrEmpty()){
                txt_empty.visibility = View.GONE

            }else{
                txt_empty.visibility = View.VISIBLE
            }
            homeActivityAdapter?.submitList(data)
            homeActivityAdapter?.notifyDataSetChanged()
        })
    }

    override fun onResume() {
        super.onResume()
//        var latlong = Utility(this).getValueString("lat_long")
//
//        viewModel.getAddress(latlong.toString(),"retrieveAddresses",1,9,"WujWfaQEkfooEWPw0xy7","hEwHa8j4W1AIkNddDwRHFg").observe(this, Observer {data ->
//            if (data != null){
//                Log.i(TAG, "deditian data ${data.Response}")
//                var labelLocation = data.Response!!.View[0].Result[0].Location.Address.Label
//                    txt_bs_location.text = labelLocation
//                Utility(applicationContext).save("label_address",labelLocation)
//            }else{
//                Log.i(TAG, "deditian data null")
//            }
//
//        })
    }


}
