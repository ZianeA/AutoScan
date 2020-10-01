package com.meteoalgerie.autoscan.service

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.presentation.OnmBarCodeApp
import io.reactivex.Single

class NotificationWorker(context: Context, workerParams: WorkerParameters) :
    RxWorker(context, workerParams) {
    override fun createWork(): Single<Result> {
        val builder = NotificationCompat.Builder(applicationContext, OnmBarCodeApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_cloud_done)
            .setContentTitle(applicationContext.getString(R.string.notification_title_sync_complete))
            .setColor(
                ContextCompat.getColor(applicationContext, R.color.notification_icon_color)
            )
            .setContentText(applicationContext.getString(R.string.notification_description_sync_complete))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(applicationContext).notify(405, builder.build())

        return Single.just(Result.success())
    }
}