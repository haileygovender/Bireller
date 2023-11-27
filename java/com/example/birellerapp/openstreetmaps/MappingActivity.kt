package com.example.birellerapp.openstreetmaps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.GpsStatus
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import com.example.birellerapp.R
import com.example.birellerapp.observation.AddObservation
import com.example.birellerapp.observation.ObserveDataClass
import com.example.birellerapp.settings.SettingsClass
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlin.math.pow

class MappingActivity : AppCompatActivity(), IMyLocationProvider , MapListener
    , GpsStatus.Listener {
    // variables
    private val LOCATION_REQUEST_CODE = 0
    private lateinit var mapView: MapView
    private lateinit var mapController: IMapController
    private lateinit var mMyLocationOverlay: MyLocationNewOverlay
    private lateinit var btnView: FloatingActionButton
    private lateinit var btnViewHotspots: FloatingActionButton
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    val userId = mAuth.currentUser?.uid
    private val settingRef = database.getReference("settings").child(userId.toString())
    private val hotspotRef = database.getReference("HotspotLocations")
    var userDistance =""
    private var isMetricUnits = true
    private val birdMarkers = mutableListOf<Marker>()
    private val birdLocation = mutableListOf<Pair<String, GeoPoint>>()
    private val hotspotLocations = mutableListOf<Pair<String, GeoPoint>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapping)
        //typecasting
        mapView = findViewById(R.id.viewMap)
        btnView = findViewById(R.id.btnView)
        btnViewHotspots = findViewById(R.id.btnViewHotspots)

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.mapCenter
        mapView.setMultiTouchControls(true)
        mapView.getLocalVisibleRect(Rect())

        mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        mapController = mapView.controller


        mMyLocationOverlay.enableMyLocation()
        mMyLocationOverlay.enableFollowLocation()
        mMyLocationOverlay.isDrawAccuracyEnabled = true


        // set the initial zoom level
        mapController.setZoom(6.0)
        // Initialize mapView and add map listener
        mapView.overlays.add(mMyLocationOverlay)
        setupMap()
        mapView.addMapListener(this)

       // markerInfoWindow = MarkerInfoWindow(R.layout.bonuspack_bubble, mapView)

        // call your method, checks and requests location permissions
        managePermissions()


                btnView.setOnClickListener {

                    val birdRef = FirebaseDatabase.getInstance().getReference("Images")

                    birdRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            birdLocation.clear() // Clear the existing bird markers
                            if (snapshot.exists()) {
                                for (birdSnapshot in snapshot.children) {
                                    val bird = birdSnapshot.getValue(ObserveDataClass::class.java)
                                    if (bird != null && bird.id == userId) {
                                        val location = bird.location
                                        val latitude = bird.latitude
                                        val longitude = bird.longitude
                                        if (location != null && latitude != null && longitude != null) {
                                            // Add bird marker data to the birdLocation list
                                            birdLocation.add(
                                                Pair(
                                                    location,
                                                    GeoPoint(
                                                        latitude.toDouble(),
                                                        longitude.toDouble()
                                                    )
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            // After retrieving bird markers, call the function to add them to the map
                            addBirdMarkers()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle database errors if needed
                        }
                    })
                }


// First, retrieve hotspot locations

        btnViewHotspots.setOnClickListener {
            if (userId != null) {
                retrieveHotspotData {
                    // Callback is invoked after hotspot data retrieval is complete

                    // Now, check user settings and display hotspots accordingly
                    settingRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val settings = snapshot.getValue(SettingsClass::class.java)
                                if (settings != null && settings.userID == userId) {
                                    userDistance = settings.distance.toString()
                                    isMetricUnits = settings.travel
                                }
                            }

                            val distanceMeasured = userDistance.toDouble()
                            if (distanceMeasured != 0.0) {
                                val startPoint = mMyLocationOverlay.myLocation
                                if (startPoint != null) {
                                    // The location is not null, proceed with the distance calculation and hotspot display
                                    val userLatitude = startPoint.latitude
                                    val userLongitude = startPoint.longitude
                                    val distanceMeasured = userDistance.toDouble()
                                    try {
                                        displayNearbyHotspots(
                                            GeoPoint(userLatitude, userLongitude),
                                            distanceMeasured,
                                            isMetricUnits
                                        )
                                    } catch (e: NumberFormatException) {
                                        // Handle the case where userDistance is not a valid double
                                        Toast.makeText(
                                            this@MappingActivity,
                                            "Invalid distance format",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    // The location is null, show a Toast message indicating that the location is not available
                                 /*   Toast.makeText(
                                        this@MappingActivity,
                                        "Location is null",
                                        Toast.LENGTH_SHORT
                                    ).show()*/
                                }
                            } else {
                                displayHotspots()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle database errors if needed
                        }
                    })
                }
            }
        }
    }

    private fun setupMap() {
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        mapController = mapView.controller
        mapView.setMultiTouchControls(true)

        // initialize the map with a start point
        val startPoint = GeoPoint(-29.8587, 31.0218)
        mapController.setCenter(startPoint)
        mapController.setZoom(6.0)

        // create a marker
        val marker = Marker(mapView)
        marker.position = startPoint // where you want the marker
        // marker.title = "Your location"
        marker.icon =
            ResourcesCompat.getDrawable(resources, R.drawable.baseline_add_location_alt_24, null)


        // add marker to map view
        mapView.overlays.add(marker)
    }


    private fun displayNearbyHotspots(
        userLocation: GeoPoint,
        maxDistanceKm: Double,
        isMetric: Boolean
    ) {
        // Clear existing overlays before adding new markers
        mapView.overlays.clear()
        mapView.overlays.add(mMyLocationOverlay) // Re-add the user's location overlay

        for ((name, location) in hotspotLocations) {
            // Check if the hotspot is within the specified distance
            val isWithinRadius =
                isHotspotWithinRadius(userLocation, location, maxDistanceKm, isMetric)
            if (isWithinRadius) {
                val hotspotMarker = Marker(mapView)
                hotspotMarker.position = location
                hotspotMarker.title = name
                // Customize the hotspot marker icon if needed
                hotspotMarker.icon = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.baseline_add_location_alt_24,
                    null
                )

                // Create a custom info window
                hotspotMarker.infoWindow = CustomInfoWindow(mapView)

                mapView.overlays.add(hotspotMarker)
            }
        }

        mapView.invalidate()
    }

    private fun displayHotspots() {
        // Clear existing overlays before adding new markers
        mapView.overlays.clear()
        mapView.overlays.add(mMyLocationOverlay) // Re-add the user's location overlay

        for ((name, location) in hotspotLocations) {
            // Check if the hotspot is within the specified distance
            val hotspotMarker = Marker(mapView)
            hotspotMarker.position = location
            hotspotMarker.title = name
            // Customize the hotspot marker icon if needed
            hotspotMarker.icon = ResourcesCompat.getDrawable(
                resources,
                R.drawable.baseline_add_location_alt_24,
                null
            )

            // Create a custom info window
            hotspotMarker.infoWindow = CustomInfoWindow(mapView)

            mapView.overlays.add(hotspotMarker)
        }


        mapView.invalidate()
    }


    private fun addBirdMarkers() {
        // Clear the existing bird markers
        mapView.overlays.removeAll(birdMarkers)

        // Iterate over the birdLocation list and add a marker for each bird
        birdLocation.forEach { (location, geoPoint) ->
            val birdMarker = Marker(mapView)
            birdMarker.position = geoPoint
            birdMarker.title = location
            birdMarker.icon=ResourcesCompat.getDrawable(
                resources,R.drawable.baseline_location_searching_24,null)


            // Add the bird marker to the map
            birdMarkers.add(birdMarker)

        }
        mapView.overlays.addAll(birdMarkers)
        // Invalidate the map to redraw the markers
        mapView.invalidate()


    }

    private fun isHotspotWithinRadius(
        userLocation: GeoPoint,
        hotspotLocation: GeoPoint,
        maxDistance: Double,
        isMetric: Boolean
    ): Boolean {
        val distance = calculateHaversineDistance(userLocation, hotspotLocation)

        return if (isMetric) {
            // The distance is already in kilometers
            distance <= maxDistance
        } else {
            // Convert kilometers to miles (1 kilometer ≈ 0.621371 miles)
            val distanceInMiles = distance * 0.621371
            distanceInMiles <= maxDistance
        }
    }

    private fun calculateHaversineDistance(
        userLocation: GeoPoint,
        hotspotLocation: GeoPoint
    ): Double {
        val lat1 = Math.toRadians(userLocation.latitude)
        val lon1 = Math.toRadians(userLocation.longitude)
        val lat2 = Math.toRadians(hotspotLocation.latitude)
        val lon2 = Math.toRadians(hotspotLocation.longitude)

        val dlon = lon2 - lon1
        val dlat = lat2 - lat1

        val a =
            Math.sin(dlat / 2).pow(2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon / 2).pow(2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        // Radius of the Earth in kilometers
        val radius = 6371.0

        return radius * c
    }


    // Handle permissions
    private fun isLocationPermissionGranted(): Boolean {
        val fineLocation = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocation && coarseLocation
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                for (result in grantResults) {
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        // Handle granted permissions
                        // You can re-initialize the map here if needed
                        // setMap()
                    } else {
                        Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun managePermissions() {
        val requestPermissions = mutableListOf<String>()
        if (!isLocationPermissionGranted()) {
            // If these permissions weren't granted
            requestPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (requestPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                requestPermissions.toTypedArray(),
                LOCATION_REQUEST_CODE
            )
        }
    }


    private fun retrieveHotspotData(callback: () -> Unit) {
        hotspotRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                hotspotLocations.clear() // Clear the existing hotspot locations
                if (snapshot.exists()) {
                    for (hotspotSnapshot in snapshot.children) {
                        val hotspot = hotspotSnapshot.getValue(Hotspot::class.java)
                        if (hotspot != null) {
                            // Create a GeoPoint from latitude and longitude
                            val geoPoint = GeoPoint(hotspot.latitude, hotspot.longitude)
                            hotspotLocations.add(Pair(hotspot.name, geoPoint))
                        }
                    }
                }
                // Callback to indicate hotspot data retrieval is complete
                callback.invoke()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database errors if needed
            }
        })
    }





    override fun startLocationProvider(myLocationConsumer: IMyLocationConsumer?): Boolean {
        return true
    }

    override fun stopLocationProvider() {
        // Stop location provider here
    }

    override fun getLastKnownLocation(): Location {
        return Location("last_known_location")
    }

    override fun destroy() {
        // Destroy resources here
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        return false
    }

    override fun onGpsStatusChanged(p0: Int) {
        // Handle GPS status changes here
    }


// Define a custom info window class


    // Modify your CustomInfoWindow class
    inner class CustomInfoWindow(mapView: MapView) : InfoWindow(
        R.layout.infor_window, mapView
    ) {

        override fun onOpen(item: Any?) {
            if (item is Marker) {
                val context = mapView.context
                val locationNameTextView = mView.findViewById<TextView>(R.id.tvLocationName)
                val timeTextView = mView.findViewById<TextView>(R.id.tvLocationTime)
                val distanceTextView = mView.findViewById<TextView>(R.id.tvDistance)
                val directionsButton = mView.findViewById<FloatingActionButton>(R.id.btnDirection)
                val addObservationButton = mView.findViewById<FloatingActionButton>(R.id.btnAdd)
                val closeButton = mView.findViewById<Button>(R.id.btnCancel)

                // Retrieve the latitude and longitude from the marker's position
                val latitude = item.position.latitude.toString()
                val longitude = item.position.longitude.toString()

                fun formatDuration(durationSeconds: Long): String {
                    val hours = durationSeconds / 3600
                    val minutes = (durationSeconds % 3600) / 60
                    val seconds = durationSeconds % 60
                    return if (hours > 0) {
                        String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    } else {
                        String.format("%02d:%02d", minutes, seconds)
                    }
                }

                fun formatDistance(distanceMeters: Double): String {
                    return if (isMetricUnits) {
                        String.format("%.2f km", distanceMeters / 1000)

                    } else {
                        val distanceMiles = distanceMeters / 1609.344
                        String.format("%.2f miles", distanceMiles)
                    }
                }


                if (mMyLocationOverlay.myLocation != null) {
                    val startPoint = GeoPoint(
                        mMyLocationOverlay.myLocation.latitude,
                        mMyLocationOverlay.myLocation.longitude
                    )

                    GlobalScope.launch(Dispatchers.IO) {
                        val roadManager = OSRMRoadManager(context, "OBP_Tuto/1.0")
                        var road: Road? = null
                        var retryCount = 0
                        val endpoint = GeoPoint(item.position.latitude, item.position.longitude)

                        while (road == null && retryCount < 3) {
                            road = try {
                                roadManager.getRoad(arrayListOf(startPoint, endpoint))
                            } catch (e: Exception) {
                                null
                            }
                            retryCount++
                        }

                        withContext(Dispatchers.Main) {
                            if (road != null && road!!.mStatus == Road.STATUS_OK) {
                                locationNameTextView.text = item.title
                                distanceTextView.text = "Distance: ${formatDistance(road!!.mLength)}"
                                timeTextView.text =
                                    "Duration: ${formatDuration(road!!.mDuration.toLong())}"

                            } else {
                                Toast.makeText(
                                    context,
                                    "Error when loading road - status= ${road?.mStatus ?: "unknown"}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Location loading error", Toast.LENGTH_SHORT).show()
                }

                directionsButton.setOnClickListener {
                    if (mMyLocationOverlay.myLocation != null) {
                        GlobalScope.launch(Dispatchers.IO) {
                            val roadManager = OSRMRoadManager(context, "OBP_Tuto/1.0")
                            var road: Road? = null
                            var retryCount = 0
                            val startPoint =
                                GeoPoint(mMyLocationOverlay.myLocation.latitude, mMyLocationOverlay.myLocation.longitude)
                            val endpoint = GeoPoint(item.position.latitude, item.position.longitude)

                            while (road == null && retryCount < 3) {
                                road = try {
                                    roadManager.getRoad(arrayListOf(startPoint, endpoint))
                                } catch (e: Exception) {
                                    null
                                }
                                retryCount++
                            }

                            withContext(Dispatchers.Main) {
                                if (road != null && road?.mStatus == Road.STATUS_OK) {
                                    val roadOverlay = RoadManager.buildRoadOverlay(road)
                                    mapView.overlays.add(roadOverlay)
                                    mapView.invalidate()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Error when loading road - status= ${road?.mStatus ?: "unknown"}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Location loading error", Toast.LENGTH_SHORT).show()
                    }
                }

                addObservationButton.setOnClickListener {
                    // Create an Intent to navigate to ObservationActivity
                    val intent = Intent(context, AddObservation::class.java)

                    // Pass the location name to ObservationActivity
                    intent.putExtra("addLocation", locationNameTextView.text)
                    intent.putExtra("addLatitude", latitude)
                    intent.putExtra("addLongitude", longitude)

                    // Start ObservationActivity
                    context.startActivity(intent)
                }

                closeButton.setOnClickListener {
                    close()
                }
            }
        }

        override fun onClose() {

        }
    }

    data class Hotspot(val name: String = "", val latitude: Double = 0.0, val longitude: Double = 0.0)

}