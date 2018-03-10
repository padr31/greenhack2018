package com.treecio.squirrel.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.Toast
import com.treecio.squirrel.NetworkClient
import com.treecio.squirrel.R
import com.treecio.squirrel.gps.LocationTrackingService
import com.treecio.squirrel.model.PlantedTree
import com.treecio.squirrel.model.TreeData
import com.treecio.squirrel.model.TreeType
import kotlinx.android.synthetic.main.fragment_plant.*
import kotlinx.android.synthetic.main.fragment_plant.view.*

private val networkClient = NetworkClient()

class PlantFragment : BaseFragment() {
    var mService: LocationTrackingService? = null
    var mBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var selected: Int = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_plant, container, false)

        view.btn_plant.setOnClickListener {
            sendPlantRequest()
        }

        val gridview = view.grid_view
        gridview.adapter = ImageAdapter(this.context)

        gridview.onItemClickListener = OnItemClickListener { parent, v, position, id ->
            if (gridview.isItemChecked(position)) {
                gridview.setItemChecked(position, false)
            } else {
                gridview.setItemChecked(position, true)
                selected = position
            }
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
        val type = selected

        val lat = location.latitude + (Math.random() - 1.0 / 2) / 10000.0
        val lon = location.longitude + (Math.random() - 1.0 / 2) / 10000.0

        val plantedTree = PlantedTree(name = name, story = story, treetype = type, time = System.currentTimeMillis(), lat = lat, lon = lon)
        TreeData.forest.add(plantedTree)
        networkClient.plant(plantedTree)

        activity.finish()

    }

    inner class ImageAdapter(private val mContext: Context) : BaseAdapter() {


        override fun getCount(): Int {
            return TreeType.values().size
        }

        override fun getItem(position: Int): Any? {
            return TreeType.values().get(position)
        }

        override fun getItemId(position: Int): Long {
            return TreeType.values()[position].ordinal.toLong()
        }

        // create a new ImageView for each item referenced by the Adapter
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val imageView: ImageView
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = ImageView(mContext)
                //imageView.setLayoutParams(GridView.LayoutParams(85, 85))
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER)

                imageView.setPadding(8, 8, 8, 8)
            } else {
                imageView = (convertView as ImageView?)!!
            }

            imageView.setImageResource(TreeType.values().get(position).fileName)

            return imageView
        }
    }


}// Required empty public constructor
