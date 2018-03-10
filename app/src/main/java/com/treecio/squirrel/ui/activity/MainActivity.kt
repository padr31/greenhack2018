package com.treecio.squirrel.ui.activity

import android.os.Bundle
import com.treecio.squirrel.R
import com.treecio.squirrel.ui.fragment.MainFragment

class MainActivity : BaseActivity() {

    companion object {
        const val MAP_FRAGMENT_TAG = "fragment_map"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init(savedInstanceState)
    }

    override fun initNew() {
        val fragment = MainFragment()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content, fragment, MAP_FRAGMENT_TAG)
                .commit()
    }
}
