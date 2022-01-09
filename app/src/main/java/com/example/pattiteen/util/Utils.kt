package com.example.pattiteen.util

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object Utils {
    private lateinit var application: Application
    fun init(app: Application) {
        application = app
    }

    private var mainThreadHandler = Handler(Looper.getMainLooper())
    private var toast: Toast? = null

    private const val PREFS_FILE = "patti3"
    private val prefManager by lazy {
        application.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
    }

    private const val PREF_USERNAME = "username"

    fun showToast(message: String) = mainThreadHandler.post {
        toast?.cancel()
        toast = Toast.makeText(application, message, Toast.LENGTH_SHORT)
            .apply { show() }
    }

    fun setUserName(username: String) {
        prefManager.edit().putString(PREF_USERNAME, username).apply()
    }

    fun getUserName(default: String = "Mangal"): String {
        return prefManager.getString(PREF_USERNAME, default) ?: default
    }
}