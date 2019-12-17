package com.example.onmbarcode.service

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.example.onmbarcode.data.equipment.EquipmentRepository
import io.reactivex.Single
import javax.inject.Inject

class SyncWorker(
    private val equipmentRepo: EquipmentRepository,
    context: Context,
    workerParams: WorkerParameters
) :
    RxWorker(context, workerParams) {
    override fun createWork(): Single<Result> {
        return equipmentRepo.getUnsyncedEquipments()
            .flatMapCompletable { equipmentRepo.updateEquipments(it) }
            .andThen(Single.just(Result.success()))
            .doOnError { println(it.message) }
    }
}