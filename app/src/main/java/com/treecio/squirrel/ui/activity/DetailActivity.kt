package com.treecio.squirrel.ui.activity

import android.os.Bundle
import android.widget.Toast
import com.treecio.squirrel.R
import com.treecio.squirrel.model.TreeData
import kotlinx.android.synthetic.main.activity_detail.*
import timber.log.Timber

class DetailActivity : BaseActivity() {

    companion object {
        const val EXTRA_TREE_ID = "extra_tree_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        init(savedInstanceState)

        val id = intent.getStringExtra(EXTRA_TREE_ID)

        val tree = TreeData.forest.orEmpty().firstOrNull { it.id == id }

        if (tree == null) {
            Toast.makeText(this, "no tree with id $id", Toast.LENGTH_SHORT).show()
            Timber.w("no tree with id $id")
            finish()
        } else {
            txt_name.text = tree.name
            txt_story.text = tree.story
        }

    }

}
