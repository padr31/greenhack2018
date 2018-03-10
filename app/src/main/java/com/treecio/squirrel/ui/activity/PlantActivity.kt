package com.treecio.squirrel.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.treecio.squirrel.R
import com.treecio.squirrel.ui.fragment.PlantFragment

class PlantActivity : AppCompatActivity() {

    private var mFragmentTransaction: FragmentTransaction? = null
    private var mFragmentManager: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant)

        val mFragmentManager: FragmentManager = supportFragmentManager
        val mFragmentTransaction: FragmentTransaction = mFragmentManager.beginTransaction()
        mFragmentTransaction.replace(R.id.content_frame, PlantFragment())
        mFragmentTransaction.commit()
    }
}
