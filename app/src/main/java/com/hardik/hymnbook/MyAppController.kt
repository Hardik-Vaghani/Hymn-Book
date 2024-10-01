package com.hardik.hymnbook

import android.app.Application
import android.util.Log
import com.hardik.hymnbook.common.Constants.BASE_TAG
import com.hardik.hymnbook.common.FragmentUtils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyAppController : Application() {
    private val TAG = BASE_TAG + MyAppController::class.java.simpleName
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
        FragmentUtils.getInstance()
    }
}