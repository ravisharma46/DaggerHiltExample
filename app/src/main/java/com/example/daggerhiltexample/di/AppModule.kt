package com.example.daggerhiltexample.di

import android.app.Application
import com.google.firebase.FirebaseApp // Added import
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppModule : Application() {
    override fun onCreate() { // Added onCreate method
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}