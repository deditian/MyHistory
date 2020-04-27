package com.dedi.myhistory.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory

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
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.dedi.myhistory.util.Utility
import com.mapbox.android.core.location.*
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Response


class MapLocation : AppCompatActivity(),OnMapReadyCallback,PermissionsListener, MapboxMap.OnMapClickListener {
    val TAG = "MapLocation"
    lateinit var mContext: Context
    lateinit var mMapview : MapView
    lateinit var locationEngine : LocationEngine
     var navigationMapRoute: NavigationMapRoute? = null

    private var currentRoute : DirectionsRoute? = null

    private val viewModel: MapViewModel by inject()

    private val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    private val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 10
//    var callback : MapLocationCallback = MapLocationCallback(this)
    var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mapboxMap: MapboxMap



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "pk.eyJ1IjoiZGVkaXRpYW4iLCJhIjoiY2s0anhxemU0MGpvcjNqbnZ0Zmhoa29udyJ9.W2wQYGoK5O-AmdHg1VlPUA")
        setContentView(R.layout.activity_map_location)
        mMapview = findViewById(R.id.mapView)
        mMapview.onCreate(savedInstanceState)
        mMapview.getMapAsync(this)

        select_location_button.setOnClickListener {
            startNavigationButton()
        }
    }

    fun startNavigationButton(){
        val simulateroute = true
        val navigationLauncherOptions = NavigationLauncherOptions.builder()

            .directionsRoute(currentRoute)
            .shouldSimulateRoute(simulateroute)
            .build()

        NavigationLauncher.startNavigation(this@MapLocation, navigationLauncherOptions)
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
            .setNegativeButton("No"
            ) { dialog, _ -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    @SuppressLint("MissingPermission")
    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this)

        val request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()

//        locationEngine.requestLocationUpdates(request, callback, mainLooper)
//        locationEngine.getLastLocation(callback)
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

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(this, R.color.mapboxGreen))
                .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(this, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()

            mapboxMap.locationComponent.apply {
                activateLocationComponent(locationComponentActivationOptions)
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING
                renderMode = RenderMode.COMPASS
                initLocationEngine()
            }

        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }

    }



    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.TRAFFIC_NIGHT) { style ->
            statusCheckGPS()
            enableLocationComponent(style)
            addDestinationIconLayer(style)
            mapboxMap.addOnMapClickListener(this)
        }
    }

    private fun addDestinationIconLayer(style: Style) {
        style.addImage("destination-icon-id",BitmapFactory.decodeResource(this.resources, R.drawable.mapbox_marker_icon_default))

        val geoJsonSource = GeoJsonSource("destination-source-id")
        style.addSource(geoJsonSource)

        val destionationSymbolLayer = SymbolLayer("destination-symbol-layer-id","destination-source-id")
        destionationSymbolLayer.withProperties(iconImage("destination-icon-id"), iconAllowOverlap(true),
            iconIgnorePlacement(true))
        style.addLayer(destionationSymbolLayer)

    }

    override fun onMapClick(point: LatLng): Boolean {
        val destinationPoint = Point.fromLngLat(point.longitude, point.latitude)

        val originPoint = Point.fromLngLat(mapboxMap.locationComponent.lastKnownLocation!!.longitude, mapboxMap.locationComponent.lastKnownLocation!!.latitude)

        val source =  mapboxMap.style?.getSourceAs<GeoJsonSource>("destination-source-id")

        source?.setGeoJson(Feature.fromGeometry(destinationPoint))

        getRoute(originPoint, destinationPoint)
        select_location_button.apply {
            isEnabled = true
            setBackgroundResource(R.color.mapbox_blue)
        }

        return true
    }

    private fun getRoute(originPoint: Point?, destinationPoint: Point?) {
        NavigationRoute.builder(this)
            .accessToken(Mapbox.getAccessToken().toString())
            .origin(originPoint!!)
            .destination(destinationPoint!!)
            .build()
            .getRoute(object : retrofit2.Callback<DirectionsResponse>{
                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Log.e(TAG, "Error: " + t.message)
                }

                override fun onResponse(
                    call: Call<DirectionsResponse>,
                    response: Response<DirectionsResponse>
                ) {

                    Log.d(TAG, "Response code: " + response.code());
                    if (response.body() == null) {
                        Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                        return;
                    } else if (response.body()!!.routes().size < 1) {
                        Log.e(TAG, "No routes found");
                        Log.e(TAG, "No routes found");
                        return;
                    }



//                    if(response.body() != null && response.body()!!.routes().size < 1){
                        currentRoute = response.body()!!.routes().get(0)
                    Log.i(TAG, "deditian  currentRoute $currentRoute")
                        if(navigationMapRoute!=null){
                            navigationMapRoute!!.removeRoute()
                        }
                        else{
                            navigationMapRoute = NavigationMapRoute(null, mapView, mapboxMap,R.style.NavigationMapRoute)
                        }

                        navigationMapRoute!!.addRoute(currentRoute)

//                    }
                }

            })
    }





//    class MapLocationCallback internal constructor(activity: MapLocation) :
//        LocationEngineCallback<LocationEngineResult> {
//        private val activityWeakReference: WeakReference<MapLocation> = WeakReference(activity)
//        override fun onSuccess(result: LocationEngineResult) {
//            val activity = activityWeakReference.get()
//            if (activity != null) {
//                val location = result.lastLocation
//                if (location == null) {
//                    return
//                }
//                Log.i("deditian", "lat : "+result.lastLocation!!.latitude.toString() +"   long :"+ result.lastLocation!!.longitude.toString())
//                Utility(activity.application).save("lat_long","${result.lastLocation!!.latitude},${result.lastLocation!!.longitude}")
//                // Pass the new location to the Maps SDK's LocationComponent
//                if (activity.mapboxMap != null && result.lastLocation != null) {
//                    activity.mapboxMap.locationComponent
//                        .forceLocationUpdate(result.lastLocation)
//                }
//            }
//        }
//        override fun onFailure(@NonNull exception: Exception) {
//            Log.d("LocationChangeActivity", exception.localizedMessage)
//            val activity = activityWeakReference.get()
//            if (activity != null) {
//                Toast.makeText(
//                    activity, exception.localizedMessage,
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }

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
