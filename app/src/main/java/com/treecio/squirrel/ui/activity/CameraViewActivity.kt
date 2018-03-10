package com.treecio.squirrel.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.*
import android.hardware.Camera
import android.location.Location
import android.os.Bundle
import android.view.SurfaceHolder
import android.widget.Toast
import com.google.android.gms.location.*
import com.treecio.squirrel.R
import com.treecio.squirrel.ar.AugmentedPOI
import com.treecio.squirrel.ar.MyCurrentAzimuth
import com.treecio.squirrel.ar.OnAzimuthChangedListener
import com.treecio.squirrel.ar.OnLocationChangedListener
import kotlinx.android.synthetic.main.activity_cameraview.*
import timber.log.Timber
import java.io.IOException
import java.util.*


class CameraViewActivity : Activity(), SurfaceHolder.Callback, OnLocationChangedListener, OnAzimuthChangedListener {

    private var mCamera: Camera? = null
    private var isCameraviewOn = false

    private var mAzimuthReal = 0.0
    private var mAzimuthTeoretical = 0.0
    private var mMyLatitude = 0.0
    private var mMyLongitude = 0.0

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var myCurrentAzimuth: MyCurrentAzimuth? = null


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cameraview)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                mMyLatitude = locationResult.lastLocation.latitude
                mMyLongitude = locationResult.lastLocation.longitude
                Timber.d("Location updated: $mMyLatitude, $mMyLongitude")
            }
        }

        myCurrentAzimuth = MyCurrentAzimuth(this, this)
        window.setFormat(PixelFormat.UNKNOWN)
        cameraview.holder.addCallback(this)
    }

    private fun calculateTeoreticalAzimuth(poi: AugmentedPOI): Double {
        val dX = poi.poiLatitude - mMyLatitude
        val dY = poi.poiLongitude - mMyLongitude

        var phiAngle: Double
        val tanPhi: Double
        var azimuth = 0.0

        tanPhi = Math.abs(dY / dX)
        phiAngle = Math.atan(tanPhi)
        phiAngle = Math.toDegrees(phiAngle)

        if (dX > 0 && dY > 0) { // I quater
            azimuth = phiAngle
        } else if (dX < 0 && dY > 0) { // II
            azimuth = 180 - phiAngle
        } else if (dX < 0 && dY < 0) { // III
            azimuth = 180 + phiAngle
        } else if (dX > 0 && dY < 0) { // IV
            azimuth = 360 - phiAngle
        }

        return phiAngle
    }

    private fun calculateAzimuthAccuracy(azimuth: Double): List<Double> {
        var minAngle = azimuth - AZIMUTH_ACCURACY
        var maxAngle = azimuth + AZIMUTH_ACCURACY
        val minMax = ArrayList<Double>()

        if (minAngle < 0)
            minAngle += 360.0

        if (maxAngle >= 360)
            maxAngle -= 360.0

        minMax.clear()
        minMax.add(minAngle)
        minMax.add(maxAngle)

        return minMax
    }

    private fun isBetween(minAngle: Double, maxAngle: Double, azimuth: Double): Boolean {
        if (minAngle > maxAngle) {
            if (isBetween(0.0, maxAngle, azimuth) || isBetween(minAngle, 360.0, azimuth))
                return true
        } else {
            if (azimuth > minAngle && azimuth < maxAngle)
                return true
        }
        return false
    }

    private fun updateDescription(poi: AugmentedPOI) {
        descriptionTextView.text =
                "${poi.poiName} azimuthTeoretical $mAzimuthTeoretical azimuthReal $mAzimuthReal latitude $mMyLatitude longitude $mMyLongitude"
    }

    override fun onLocationChanged(location: Location) {
        mMyLatitude = location.latitude
        mMyLongitude = location.longitude
        //mAzimuthTeoretical = calculateTeoreticalAzimuth()
        Toast.makeText(this, "latitude: " + location.latitude + " longitude: " + location.longitude, Toast.LENGTH_SHORT).show()
        //updateDescription()
        redraw()
    }

    override fun onAzimuthChanged(azimuthChangedTo: Float) {
        mAzimuthReal = azimuthChangedTo.toDouble()
        Timber.d("Azimuth changed: $mAzimuthReal")
        //mAzimuthTeoretical = calculateTeoreticalAzimuth()

        /*pointerIcon = findViewById<View>(R.id.icon) as ImageView

        val minAngle = calculateAzimuthAccuracy(mAzimuthTeoretical)[0]
        val maxAngle = calculateAzimuthAccuracy(mAzimuthTeoretical)[1]

        if (isBetween(minAngle, maxAngle, mAzimuthReal)) {
            pointerIcon.visibility = View.VISIBLE
        } else {
            pointerIcon.visibility = View.INVISIBLE
        }

        updateDescription()*/
        redraw()
    }

    fun redraw() {
        val v = canvas
        val b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        val paint = Paint()

        paint.setStyle(Paint.Style.FILL)
        paint.setColor(Color.WHITE)
        c.drawCircle(v.getMeasuredWidth() / 2.0F, v.getMeasuredHeight() / 2.0F, v.getMeasuredWidth() / 4.0F, paint)
        v.draw(c)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
        myCurrentAzimuth!!.start()
    }

    override fun onStop() {
        myCurrentAzimuth!!.stop()
        super.onStop()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int,
                                height: Int) {
        if (isCameraviewOn) {
            mCamera!!.stopPreview()
            isCameraviewOn = false
        }

        if (mCamera != null) {
            try {
                mCamera!!.setPreviewDisplay(cameraview.holder)
                mCamera!!.startPreview()
                isCameraviewOn = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mCamera = Camera.open()
        mCamera!!.setDisplayOrientation(90)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mCamera!!.stopPreview()
        mCamera!!.release()
        mCamera = null
        isCameraviewOn = false
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 1000
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null /* Looper */)
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        private val AZIMUTH_ACCURACY = 5.0
    }
}
