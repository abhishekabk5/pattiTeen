package com.example.pattiteen.util

import android.util.Log
import com.mocklets.pluto.PlutoLog

object Logr {
    private const val TAG = "--"

    fun i(msg: String) { i(TAG, msg) }
    fun i(tag: String, msg: String) {
        PlutoLog.i(tag, msg)
        Log.i(tag, msg)
        Utils.showToast("$tag: $msg")
    }
}