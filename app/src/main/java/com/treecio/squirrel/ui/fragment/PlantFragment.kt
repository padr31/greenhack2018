package com.treecio.squirrel.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.treecio.squirrel.NetworkClient
import com.treecio.squirrel.R
import kotlinx.android.synthetic.main.fragment_plant.*
import com.treecio.squirrel.gps.LocationTrackingService
import com.treecio.squirrel.model.PlantedTree
import kotlinx.android.synthetic.main.fragment_plant.view.*


private val networkClient = NetworkClient()

class PlantFragment : BaseFragment() {
    var mService: LocationTrackingService? = null
    var mBound = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_plant, container, false)

        view.btn_plant.setOnClickListener {
            sendPlantRequest()
        }

        return view
    }

    @SuppressLint("MissingPermission")
    private fun sendPlantRequest() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()


        val location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false))

        if (location == null) {
            Toast.makeText(context, getString(R.string.unknown_location), Toast.LENGTH_SHORT).show()
            return
        }

        val name = txt_name.text.toString()
        val story = txt_story.text.toString()
        val plantedTree = PlantedTree(name = name, story = story, time = System.currentTimeMillis(), lat = location.latitude, lon = location.longitude)
        networkClient.plant(plantedTree)

    }


}// Required empty public constructor
