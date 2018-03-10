package com.treecio.squirrel.ui.fragment

import android.support.v4.app.Fragment
import org.greenrobot.eventbus.EventBus

/**
 * Class for coupling common behavior of all fragments in the project.
 */
abstract class BaseFragment(
        val usesEventBus: Boolean = false
) : Fragment() {

    override fun onStart() {
        super.onStart()
        if (usesEventBus)
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        if (usesEventBus)
            EventBus.getDefault().unregister(this)
        super.onStop()
    }

}
