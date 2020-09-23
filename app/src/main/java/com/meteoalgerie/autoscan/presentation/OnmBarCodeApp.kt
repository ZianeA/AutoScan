package com.meteoalgerie.autoscan.presentation

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import com.meteoalgerie.autoscan.presentation.di.DaggerAppComponent
import com.meteoalgerie.autoscan.service.MyWorkerFactory
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import de.timroes.axmlrpc.XMLRPCException
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import javax.inject.Inject

class OnmBarCodeApp : MultiDexApplication(), HasActivityInjector, Configuration.Provider {

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

        // RxJava2 throws UndeliverableException when app is stopped while fetching data from server
        RxJavaPlugins.setErrorHandler {
            if (it is UndeliverableException && it.cause is XMLRPCException) {
                return@setErrorHandler
            }

            throw it
        }
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