package com.example.onmbarcode.data.equipment

import com.example.onmbarcode.data.desk.DeskRepository
import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.data.user.UserRepository
import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.equipment.Equipment
import com.example.onmbarcode.presentation.equipment.Equipment.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EquipmentRepository @Inject constructor(
    private val equipmentDao: EquipmentDao,
    private val equipmentService: EquipmentService,
    private val userRepository: UserRepository, //Should probably use userDao instead
    private val equipmentEntityMapper: Mapper<EquipmentEntity, Equipment>,
    private val equipmentResponseMapper: Mapper<HashMap<*, *>, Equipment>
) {
    fun getAllEquipmentForDesk(deskId: Int): Observable<List<Equipment>> {
        return equipmentDao.getByDesk(deskId)
            .map { e -> e.map(equipmentEntityMapper::map) }
    }

    fun getEquipmentForDeskWithScanState(
        deskId: Int,
        vararg scanState: ScanState
    ): Observable<List<Equipment>> {
        return Observable.just(scanState)
            .flatMap {
                when (it.size) {
                    1 -> equipmentDao.getByDeskAndScanState(deskId, it.first())
                    2 -> equipmentDao.getByDeskAndScanState(deskId, it[0], it[1])
                    3 -> equipmentDao.getByDesk(deskId)
                    else -> throw IllegalArgumentException("You can't filter by more than three scan states.")
                }
            }.map { e -> e.map(equipmentEntityMapper::map) }
    }

    fun findEquipment(barcode: String): Maybe<Equipment> {
        return equipmentDao.getByBarcode(barcode)
            .map(equipmentEntityMapper::map)
    }

    fun getAllUnsyncedEquipment(): Single<List<Equipment>> {
        return equipmentDao.getByScanState(ScanState.ScannedButNotSynced)
            .map { e -> e.map(equipmentEntityMapper::map) }
    }

    fun updateEquipment(equipment: Equipment): Completable {
        val scannedAndSyncedEquipment = equipment.copy(scanState = ScanState.ScannedAndSynced)
        return userRepository.getUser()
            .toSingle() //To throw an exception if there's no user.
            .flatMapCompletable { user ->
                equipmentDao.update(
                    equipmentEntityMapper.mapReverse(
                        equipment.copy(
                            scanState = ScanState.ScannedButNotSynced
                        )
                    )
                ).andThen(
                    equipmentService.update(
                        user,
                        equipment.id,
                        equipmentResponseMapper.mapReverse(scannedAndSyncedEquipment)
                    )
                )
            }
            .andThen(Completable.defer {
                //I'm getting unexpected behavior, I had to use defer.
                equipmentDao.update(equipmentEntityMapper.mapReverse(scannedAndSyncedEquipment))
            })
    }

    fun getAllEquipmentCount(): Single<Int> = equipmentDao.getAllCount()

    fun deleteAllEquipments(): Completable = equipmentDao.deleteAll()
}
