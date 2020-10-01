package com.meteoalgerie.autoscan.presentation.download

import android.app.Application
import androidx.work.*
import dagger.Reusable
import javax.inject.Inject

@Reusable
class DownloadBackgroundService @Inject constructor(private val app: Application) {
    fun downloadData() {
        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(Constraints.NONE)
            .build()

        WorkManager.getInstance(app)
            .beginUniqueWork(WORK_NAME_DOWNLOAD, ExistingWorkPolicy.KEEP, downloadWorkRequest)
            .enqueue()
    }

    companion object {
        const val WORK_NAME_DOWNLOAD = "WORK_NAME_DOWNLOAD"
    }
}