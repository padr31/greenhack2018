package com.treecio.squirrel.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.treecio.squirrel.NetworkClient
import com.treecio.squirrel.R
import kotlinx.android.synthetic.main.fragment_plant.*
import com.treecio.squirrel.gps.LocationTrackingService
import com.treecio.squirrel.model.PlantedTree
import kotlinx.android.synthetic.main.fragment_plant.view.*
import android.widget.AdapterView.OnItemClickListener

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

        val gridview = view.grid_view
        gridview.adapter = ImageAdapter(this.context)

        gridview.onItemClickListener = OnItemClickListener { parent, v, position, id ->
            gridview.setItemChecked(position, true);
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

    inner class ImageAdapter(private val mContext: Context) : BaseAdapter() {

        // references to our images
        private val mThumbIds = arrayOf<Int>(R.drawable.ic_fruit, R.drawable.ic_pine, R.drawable.ic_leave, R.drawable.ic_tree, R.drawable.ic_tulip, R.drawable.ic_palm)

        override fun getCount(): Int {
            return mThumbIds.size
        }

        override fun getItem(position: Int): Any? {
            return mThumbIds.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
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

            imageView.setImageResource(mThumbIds[position])

            return imageView
        }
    }


}// Required empty public constructor
