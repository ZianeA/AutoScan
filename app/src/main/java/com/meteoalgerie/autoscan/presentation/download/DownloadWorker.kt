package com.meteoalgerie.autoscan.presentation.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.meteoalgerie.autoscan.R
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class DownloadWorker(
    context: Context,
    parameters: WorkerParameters,
    private val downloadDataUseCase: DownloadDataUseCase
) : RxWorker(context, parameters) {

    companion object {
        const val CHANNEL_ID = "download_channel"
        const val NOTIFICATION_ID = 967486325
    }

    override fun createWork(): Single<Result> {
        setForegroundAsync(createForegroundInfo(-1, true))

        return downloadDataUseCase.execute()
            .doOnError { setForegroundAsync(createForegroundInfo(-1, true)) }
            .retryWhen { it.delay(1, TimeUnit.SECONDS) }
            .doOnNext { progress -> setForegroundAsync(createForegroundInfo(progress)) }
            .toList()
            .map { Result.success() }
    }

    private fun createForegroundInfo(
        progress: Int,
        indeterminate: Boolean = false
    ): ForegroundInfo {
        val title = applicationContext.getString(R.string.notification_title_download)
        val description = applicationContext.getString(R.string.notification_description_download)
        val cancel = applicationContext.getString(R.string.notification_cancel_download)
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(getId())

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(description)
            .setTicker(title)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setProgress(100, progress, indeterminate)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(R.drawable.ic_cancel, cancel, intent)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val mChannel = NotificationChannel(
            CHANNEL_ID,
            applicationContext.getString(R.string.notification_channel_download),
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager =
            ContextCompat.getSystemService(applicationContext, NotificationManager::class.java)!!
        notificationManager.createNotificationChannel(mChannel)
    }
}