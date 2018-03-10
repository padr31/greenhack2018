package com.treecio.squirrel.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.tinsuke.icekick.extension.freezeInstanceState
import com.tinsuke.icekick.extension.unfreezeInstanceState
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

/**
 * Class for coupling common behavior of all activities in the project.
 */
abstract class BaseActivity(
        private val usesEventBus: Boolean = false
) : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unfreezeInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        freezeInstanceState(outState)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
    }

    /**
     * This method is intended to be called from [onCreate] of the child classes, usually after
     * [setContentView]. This will either call [initNew] or [initRecycled].
     * @param savedInstanceState argument from the onCreate method
     */
    protected fun init(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            initNew()
        } else {
            initRecycled(savedInstanceState)
        }
    }

    /**
     * Will be called from [init] if the savedInstanceState is null.
     */
    protected open fun initNew() {
        // can be overridden by children
    }

    /**
     * Will be called from [init] if the savedInstanceState is not null.
     * @param state savedInstanceState object passed to [init]
     */
    protected open fun initRecycled(state: Bundle) {
        // can be overridden by children
    }

    override fun onStart() {
        super.onStart()
        Timber.d("[" + javaClass.simpleName + " start]")
        if (usesEventBus)
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        if (usesEventBus)
            EventBus.getDefault().unregister(this)
        //Timber.d("[" + javaClass.simpleName + " stop]")
        super.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
