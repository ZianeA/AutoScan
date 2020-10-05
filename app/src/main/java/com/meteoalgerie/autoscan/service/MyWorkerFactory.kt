package com.meteoalgerie.autoscan.service

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.meteoalgerie.autoscan.data.equipment.*
import com.meteoalgerie.autoscan.data.mapper.Mapper
import com.meteoalgerie.autoscan.presentation.download.DownloadDataUseCase
import com.meteoalgerie.autoscan.presentation.download.DownloadWorker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyWorkerFactory @Inject constructor(
    private val equipmentDao: EquipmentDao,
    private val equipmentService: EquipmentService,
    private val equipmentResponseMapper: Mapper<HashMap<*, *>, Equipment>,
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
                    equipmentService,
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