package com.treecio.squirrel.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Criteria
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.treecio.squirrel.NetworkClient
import com.treecio.squirrel.R
import com.treecio.squirrel.model.PlantedTree
import com.treecio.squirrel.ui.activity.PlantActivity
import kotlinx.android.synthetic.main.fragment_main.view.*
import timber.log.Timber


class MainFragment : BaseFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    val client = NetworkClient()

    lateinit var mMapView: MapView
    private var map: GoogleMap? = null
    private var forest: Collection<PlantedTree>? = null

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

    override fun onResume() {
        super.onResume()
        mMapView.onResume()

        client.sendFetchRequest { response ->
            setData(response.trees.orEmpty())
        }
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
        Timber.i("Got ${trees.size} trees")
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

        map!!.setOnMarkerClickListener(this)

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

    private fun showTree(tree: PlantedTree) {
        val coordinates = LatLng(tree.lat, tree.lon)

        val marker = map!!.addMarker(MarkerOptions()
                .position(coordinates)
                .title(tree.name))
        marker.tag = tree.id
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        /*val resultIntent = Intent(context, DetailActivity::class.java)
        resultIntent.putExtras(DetailActivity.Companion.getArguments(circle.tag as String?))
        context.startActivity(resultIntent)*/
        return true
    }
}
