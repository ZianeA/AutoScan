package com.example.onmbarcode.service

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.example.onmbarcode.data.equipment.EquipmentDao
import com.example.onmbarcode.data.equipment.EquipmentEntityMapper
import com.example.onmbarcode.data.equipment.EquipmentResponseMapper.Companion.ATTRIBUTE_ID_NAME
import com.example.onmbarcode.data.equipment.EquipmentService
import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.data.user.UserRepository
import com.example.onmbarcode.presentation.equipment.Equipment
import com.example.onmbarcode.presentation.equipment.Equipment.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class SyncWorker(
    private val equipmentDao: EquipmentDao,
    private val equipmentService: EquipmentService,
    private val userRepository: UserRepository,
    private val equipmentEntityMapper: EquipmentEntityMapper,
    private val equipmentResponseMapper: Mapper<HashMap<*, *>, Equipment>,
    context: Context,
    workerParams: WorkerParameters
) :
    RxWorker(context, workerParams) {
    override fun createWork(): Single<Result> {
        return userRepository.getUser()
            .toSingle()
            .flatMapCompletable { user ->
                equipmentDao.getByScanState(ScanState.ScannedButNotSynced)
                    .flatMapObservable { Observable.fromIterable(it) }
                    .map {
                        object {
                            val id = it.id
                            val equipmentResponse =
                                equipmentResponseMapper.mapReverse(
                                    equipmentEntityMapper.map(it).copy(
                                        scanState = ScanState.ScannedAndSynced
                                    )
                                )
                            val equipmentEntity = it.copy(scanState = ScanState.ScannedAndSynced)
                        }
                    }
                    .flatMapCompletable {
                        equipmentService.update(user, it.id, it.equipmentResponse)
                            .andThen(Completable.defer { equipmentDao.update(it.equipmentEntity) })
                    }
            }.andThen(Single.just(Result.success()))
    }
}