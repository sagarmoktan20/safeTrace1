package com.example.safetrace

import android.app.Application

class safetraceApplication:Application() {

    override fun onCreate() {
        super.onCreate()

        sharedPref.init(this)
    }
}