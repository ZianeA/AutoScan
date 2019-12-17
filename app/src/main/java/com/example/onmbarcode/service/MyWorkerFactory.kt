package com.example.onmbarcode.service

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.onmbarcode.data.equipment.EquipmentRepository

class MyWorkerFactory(private val equipmentRepository: EquipmentRepository) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val workerClass = Class.forName(workerClassName)
        return when {
            workerClass.isAssignableFrom(SyncWorker::class.java) -> SyncWorker(
                equipmentRepository,
                appContext,
                workerParameters
            )
            else -> workerClass.newInstance() as ListenableWorker
        }
    }
}