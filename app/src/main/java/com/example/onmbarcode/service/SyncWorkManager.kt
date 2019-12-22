package com.example.onmbarcode.service

import android.app.Application
import androidx.work.*
import com.example.onmbarcode.data.equipment.EquipmentRepository
import dagger.Reusable
import javax.inject.Inject

@Reusable
class SyncWorkManager @Inject constructor(private val app: Application) : SyncBackgroundService {
    override fun syncEquipments() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .addTag(TAG_SYNC)
            .build()

        WorkManager.getInstance(app)
            .beginUniqueWork(WORK_NAME_SYNC, ExistingWorkPolicy.KEEP, workRequest)
            .enqueue()
    }

    companion object {
        const val TAG_SYNC = "TAG_SYNC"
        private const val WORK_NAME_SYNC = "WORK_NAME_SYNC"
    }
}