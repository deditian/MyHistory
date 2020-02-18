package com.dedi.myhistory.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dedi.myhistory.R
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.activity_map_location.*

import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.dedi.myhistory.util.Utility
import com.mapbox.android.core.location.*
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import org.koin.android.ext.android.inject
import java.lang.ref.WeakReference


class MapLocation : AppCompatActivity(),OnMapReadyCallback,PermissionsListener {
    val TAG = "MapLocation"
    lateinit var mContext: Context
    lateinit var mMapview : MapView
    lateinit var locationEngine : LocationEngine
    val viewModel: MapViewModel by inject()

    val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 10
    var callback : MapLocationCallback = MapLocationCallback(this)
    var permissionsManager: PermissionsManager = PermissionsManager(this)
    lateinit var mapboxMap: MapboxMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.access_token))
        setContentView(R.layout.activity_map_location)
        mMapview = findViewById(R.id.mapView)
        mMapview.onCreate(savedInstanceState)
        mMapview.getMapAsync(this)



    }

    fun statusCheckGPS() {
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager?

        if (!manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()

        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes"
            ) { _, _ ->
                startActivity(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                )
            }
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert = builder.create()
        alert.show()
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(
            Style.LIGHT
        ) { style ->
            statusCheckGPS()
            enableLocationComponent(style)

            }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(this, R.color.mapboxGreen))
                .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(this, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()

            // Get an instance of the LocationComponent and then adjust its settings
            mapboxMap.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

            // Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

            // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

            // Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS

                initLocationEngine()
            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }

    }

    @SuppressLint("MissingPermission")
    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this)

        val request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()

        locationEngine.requestLocationUpdates(request, callback, mainLooper)
        locationEngine.getLastLocation(callback)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        var asd = permissionsToExplain?.size
        Toast.makeText(this, "apa ini : $asd", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(this, "user_location_permission_not_granted", Toast.LENGTH_LONG).show()
            finish()
        }
    }



    class MapLocationCallback internal constructor(activity: MapLocation) :
        LocationEngineCallback<LocationEngineResult> {

        private val activityWeakReference: WeakReference<MapLocation> = WeakReference(activity)

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        override fun onSuccess(result: LocationEngineResult) {
            val activity = activityWeakReference.get()

            if (activity != null) {
                val location = result.lastLocation

                if (location == null) {
                    return
                }

                Log.i("deditian", "lat : "+result.lastLocation!!.latitude.toString() +"   long :"+ result.lastLocation!!.longitude.toString())


                Utility(activity.application).save("lat_long","${result.lastLocation!!.latitude},${result.lastLocation!!.longitude}")
                // Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null && result.lastLocation != null) {
                    activity.mapboxMap.locationComponent
                        .forceLocationUpdate(result.lastLocation)
                }
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can't be captured
         *
         * @param exception the exception message
         */
        override fun onFailure(@NonNull exception: Exception) {
            Log.d("LocationChangeActivity", exception.localizedMessage)
            val activity = activityWeakReference.get()
            if (activity != null) {
                Toast.makeText(
                    activity, exception.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        val latlong = Utility(this).getValueString("lat_long")

        viewModel.getAddress(latlong.toString(),"retrieveAddresses",1,9,"WujWfaQEkfooEWPw0xy7","hEwHa8j4W1AIkNddDwRHFg").observe(this, Observer {data ->
            if (data != null){
                Log.i(TAG, "deditian data ${data.Response}")
                val labelLocation = data.Response!!.View[0].Result[0].Location.Address.Label
                txt_map_location.text = labelLocation
                Utility(applicationContext).save("label_address",labelLocation)
            }else{
                Log.i(TAG, "deditian data null")
            }

        })
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }




}
