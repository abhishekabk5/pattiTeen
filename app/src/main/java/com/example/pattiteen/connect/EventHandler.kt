package com.example.pattiteen.connect

import android.os.Bundle

interface EventHandler {
    fun handleMessage(message: Bundle)
}