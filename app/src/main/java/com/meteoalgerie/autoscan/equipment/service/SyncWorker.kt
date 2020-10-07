package com.meteoalgerie.autoscan.equipment.service

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.meteoalgerie.autoscan.equipment.EquipmentDao
import com.meteoalgerie.autoscan.equipment.Equipment.*
import com.meteoalgerie.autoscan.equipment.EquipmentResponseMapper
import com.meteoalgerie.autoscan.equipment.EquipmentApi
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class SyncWorker(
    private val equipmentDao: EquipmentDao,
    private val equipmentApi: EquipmentApi,
    private val equipmentResponseMapper: EquipmentResponseMapper,
    context: Context,
    workerParams: WorkerParameters
) :
    RxWorker(context, workerParams) {
    override fun createWork(): Single<Result> {
        return equipmentDao.getByScanState(ScanState.ScannedButNotSynced)
            .flatMapObservable { Observable.fromIterable(it) }
            .map {
                object {
                    val id = it.id
                    val equipmentResponse =
                        equipmentResponseMapper.mapReverse(it.copy(scanState = ScanState.ScannedAndSynced))
                    val equipmentEntity = it.copy(scanState = ScanState.ScannedAndSynced)
                }
            }
            .flatMapCompletable {
                equipmentApi.update(it.id, it.equipmentResponse)
                    .andThen(Completable.defer { equipmentDao.update(it.equipmentEntity) })
            }.toSingle { }

            .flatMap { Single.just(Result.success()) }
            .onErrorReturnItem(Result.retry())
    }
}