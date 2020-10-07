package com.meteoalgerie.autoscan.equipment.service

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.meteoalgerie.autoscan.download.DownloadDataUseCase
import com.meteoalgerie.autoscan.download.DownloadWorker
import com.meteoalgerie.autoscan.equipment.EquipmentDao
import com.meteoalgerie.autoscan.equipment.EquipmentResponseMapper
import com.meteoalgerie.autoscan.equipment.EquipmentApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyWorkerFactory @Inject constructor(
    private val equipmentDao: EquipmentDao,
    private val equipmentApi: EquipmentApi,
    private val equipmentResponseMapper: EquipmentResponseMapper,
    private val downloadDataUseCase: DownloadDataUseCase
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val workerClass = Class.forName(workerClassName)
        return when {
            workerClass.isAssignableFrom(SyncWorker::class.java) -> {
                SyncWorker(
                    equipmentDao,
                    equipmentApi,
                    equipmentResponseMapper,
                    appContext,
                    workerParameters
                )
            }
            workerClass.isAssignableFrom(DownloadWorker::class.java) -> {
                DownloadWorker(appContext, workerParameters, downloadDataUseCase)
            }
            else -> null //Use default workerFactory
        }
    }
}