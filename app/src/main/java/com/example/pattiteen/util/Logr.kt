package com.example.pattiteen.util

import com.mocklets.pluto.PlutoLog

object Logr {
    private const val TAG = "--"

    fun i(msg: String) { i(TAG, msg) }
    fun i(tag: String, msg: String) {
        PlutoLog.i(tag, msg)
    }
}