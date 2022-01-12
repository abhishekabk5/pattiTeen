package com.example.pattiteen

import android.app.Application
import android.util.Log
import com.example.pattiteen.util.Utils
import com.mocklets.pluto.Pluto

class PattiTeenApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Pluto.initialize(this)
        Utils.init(this)
        Pluto.setExceptionHandler { _, throwable ->
            Log.e("Pluto", throwable.localizedMessage.orEmpty())
        }
    }
}