package com.meteoalgerie.autoscan.service

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.meteoalgerie.autoscan.data.equipment.*
import com.meteoalgerie.autoscan.data.mapper.Mapper
import com.meteoalgerie.autoscan.data.user.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyWorkerFactory @Inject constructor(
    private val equipmentDao: EquipmentDao,
    private val equipmentService: EquipmentService,
    private val userRepository: UserRepository,
    private val equipmentMapper: EquipmentMapper,
    private val equipmentResponseMapper: Mapper<HashMap<*, *>, Equipment>
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val workerClass = Class.forName(workerClassName)
        return when {
            workerClass.isAssignableFrom(SyncWorker::class.java) -> SyncWorker(
                equipmentDao,
                equipmentService,
                userRepository,
                equipmentMapper,
                equipmentResponseMapper,
                appContext,
                workerParameters
            )
            else -> null //Use default workerFactory
        }
    }
}