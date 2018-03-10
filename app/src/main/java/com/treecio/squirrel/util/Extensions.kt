package com.treecio.squirrel.util

import android.content.Context
import android.os.Handler

fun Context.runOnMainThread(r: () -> Unit) {
    Handler(mainLooper).post(r)
}
