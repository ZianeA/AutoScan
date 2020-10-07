package com.meteoalgerie.autoscan.equipment.service

import android.app.Application
import androidx.work.*
import dagger.Reusable
import javax.inject.Inject

@Reusable
class SyncWorkManager @Inject constructor(private val app: Application) : SyncBackgroundService {
    override fun syncEquipments() {
        val syncConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(syncConstraints)
            .build()

        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .build()

        WorkManager.getInstance(app)
            .beginUniqueWork(WORK_NAME_SYNC, ExistingWorkPolicy.KEEP, syncWorkRequest)
            .then(notificationWorkRequest)
            .enqueue()
    }

    companion object {
        const val WORK_NAME_SYNC = "WORK_NAME_SYNC"
    }
}