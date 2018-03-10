package com.treecio.squirrel.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Criteria
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.treecio.squirrel.R
import com.treecio.squirrel.model.PlantedTree
import com.treecio.squirrel.ui.activity.PlantActivity
import kotlinx.android.synthetic.main.fragment_main.view.*


class MainFragment : BaseFragment(), OnMapReadyCallback, GoogleMap.OnCircleClickListener {

    lateinit var mMapView: MapView
    private var map: GoogleMap? = null
    private var forest: Collection<PlantedTree>? = null

    private var color: Int = 0
    private val radius: Long = 0
    private var coordinates: LatLng? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)

        mMapView = rootView.findViewById(R.id.mapView)
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume() // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(activity.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mMapView.getMapAsync(this)

        setupWidgets(rootView)

        return rootView
    }

    private fun setupWidgets(view: View) {
        val btn = view.btn_plant_tree
        btn.setOnClickListener {
            val intent = Intent(context, PlantActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        //DataHolder.INSTANCE.getPerformanceListeners().add(this)
    }

    override fun onStop() {
        //DataHolder.INSTANCE.getPerformanceListeners().remove(this)
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    fun setData(trees: Collection<PlantedTree>) {
        if (!isAdded) {
            this.forest = trees
        } else {
            this.forest = null
            for (tree in trees) {
                showTree(tree)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(mMap: GoogleMap) {
        map = mMap

        // For showing a move to my location button
        map!!.isMyLocationEnabled = true

        map!!.setOnCircleClickListener(this)

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()

        val location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false))
        if (location != null) {
            map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 13f))

            val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(location.latitude, location.longitude))      // Sets the center of the map to location user
                    .zoom(17f)                   // Sets the zoom
                    .tilt(40f)                   // Sets the tilt of the camera to 30 degrees
                    .build()                   // Creates a CameraPosition from the builder
            map!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }

        // Set retro style Google Maps
        //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json))

        if (forest != null) {
            for (tree in forest!!) {
                showTree(tree)
            }
            forest = null
        }
    }

    fun showTree(tree: PlantedTree) {
        color = Color.GREEN
        coordinates = LatLng(tree.lat, tree.lon)

        val circle = map!!.addCircle(CircleOptions()
                .center(coordinates)
                .radius(20.0)
                .strokeColor(color)
                .strokeWidth(5f)
                .fillColor(color)
        )
        circle.tag = tree.id
        //circle.isClickable = true
    }

    override fun onCircleClick(circle: Circle) {
        /*val resultIntent = Intent(context, DetailActivity::class.java)
        resultIntent.putExtras(DetailActivity.Companion.getArguments(circle.tag as String?))
        context.startActivity(resultIntent)*/
    }

}
