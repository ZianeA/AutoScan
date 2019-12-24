package com.example.onmbarcode.presentation

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Configuration
import com.example.onmbarcode.R
import com.example.onmbarcode.data.equipment.EquipmentDao
import com.example.onmbarcode.data.equipment.EquipmentEntityMapper
import com.example.onmbarcode.data.equipment.EquipmentRepository
import com.example.onmbarcode.data.equipment.EquipmentService
import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.presentation.di.DaggerAppComponent
import com.example.onmbarcode.presentation.equipment.Equipment
import com.example.onmbarcode.service.MyWorkerFactory
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class OnmBarCodeApp : Application(), HasActivityInjector, Configuration.Provider {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var workerFactory: MyWorkerFactory

    override fun onCreate() {
        DaggerAppComponent.factory()
            .create(this)
            .inject(this)

        super.onCreate()
        // From android docs "...you should execute this code as soon as your app starts.
        // It's safe to call this repeatedly
        // because creating an existing notification channel performs no operation."
        createNotificationChannel()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Sync"
            val descriptionText = "Sync finished"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "sync_channel"
    }
}