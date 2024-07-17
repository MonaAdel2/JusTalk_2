package com.example.justalk_2

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class MyApplication : Application() {

    companion object {
        lateinit var instance: MyApplication

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }

}