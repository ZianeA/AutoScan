package com.example.onmbarcode.presentation

import android.app.Activity
import android.app.Application
import com.example.onmbarcode.presentation.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class OnmBarCodeApp : Application(), HasActivityInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        DaggerAppComponent.factory()
            .create(this)
            .inject(this)

        super.onCreate()

    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector
}