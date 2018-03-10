package com.treecio.squirrel.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Criteria
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.treecio.squirrel.NetworkClient
import com.treecio.squirrel.R
import com.treecio.squirrel.model.PlantedTree
import com.treecio.squirrel.model.TreeData
import com.treecio.squirrel.model.TreeType
import com.treecio.squirrel.ui.activity.CameraViewActivity
import com.treecio.squirrel.ui.activity.DetailActivity
import com.treecio.squirrel.ui.activity.PlantActivity
import com.treecio.squirrel.util.runOnMainThread
import kotlinx.android.synthetic.main.fragment_main.view.*
import timber.log.Timber


class MainFragment : BaseFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    val client = NetworkClient()

    lateinit var mMapView: MapView
    private var map: GoogleMap? = null
    private var forestToDisplay: Collection<PlantedTree>? = null

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
        view.btn_plant_tree.setOnClickListener {
            context.startActivity(Intent(context, PlantActivity::class.java))
        }
        view.btn_ar.setOnClickListener {
            context.startActivity(Intent(context, CameraViewActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()

        client.sendFetchRequest { response ->
            TreeData.forest.addAll(response.trees.orEmpty())
            setData(TreeData.forest)
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
            this.forestToDisplay = trees
        } else {
            this.forestToDisplay = null
            showForest(trees)
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

        val forest = forestToDisplay
        if (forest != null) {
            showForest(forest)
            forestToDisplay = null
        }
    }

    private fun showForest(trees: Collection<PlantedTree>) {
        context.runOnMainThread {
            for (tree in trees) {
                showTree(tree)
            }
        }
    }

    private fun showTree(tree: PlantedTree) {
        Timber.d("Showing tree")
        val coordinates = LatLng(tree.lat, tree.lon)

        val marker = map!!.addMarker(MarkerOptions()
                .position(coordinates)
                .title(tree.name))
        marker.tag = tree.id
        marker.setIcon(getMarkerIconFromDrawable(resources.getDrawable(TreeType.values().get(tree.treetype).fileName)))
    }

    private fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor {
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        /*val resultIntent = Intent(context, DetailActivity::class.java)
        resultIntent.putExtras(DetailActivity.Companion.getArguments(circle.tag as String?))
        context.startActivity(resultIntent)*/
        val treeid = p0?.tag as String?
        /*val tree = treeid?.let { TreeData.forest.orEmpty().firstOrNull { it.id == treeid } }
        if (tree == null) {
            Toast.makeText(context, "Tree is null", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_TREE_ID, tree.id)
            context.startActivity(intent)
        }*/
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_TREE_ID, treeid)
        context.startActivity(intent)
        return true
    }
}
