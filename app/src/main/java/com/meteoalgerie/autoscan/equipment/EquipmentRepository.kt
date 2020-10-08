package com.meteoalgerie.autoscan.equipment

import com.meteoalgerie.autoscan.equipment.Equipment.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EquipmentRepository @Inject constructor(
    private val equipmentDao: EquipmentDao,
    private val equipmentApi: EquipmentApi,
    private val equipmentResponseMapper: EquipmentResponseMapper
) {

    fun getEquipmentForDeskAndScanState(
        deskId: Int,
        scanState: List<ScanState>
    ): Observable<List<Equipment>> {
        return Observable.just(scanState)
            .flatMap {
                when (it.size) {
                    1 -> equipmentDao.getByDeskAndScanState(deskId, it.first())
                    2 -> equipmentDao.getByDeskAndScanState(deskId, it[0], it[1])
                    3 -> equipmentDao.getByDesk(deskId)
                    else -> throw IllegalArgumentException("You can't filter by more than three scan states.")
                }
            }
    }

    fun refreshEquipmentForDesk(deskId: Int): Completable {
        return equipmentApi.getByDesk(deskId)
            .map { list -> list.map { item -> equipmentResponseMapper.map(item as HashMap<*, *>) } }
            .flatMap { refreshList ->
                // Prevent the refresh from overriding the not synced equipment
                equipmentDao.getByDeskAndScanState(deskId, ScanState.ScannedButNotSynced)
                    .first(emptyList())
                    // filter out the not synced equipment from the refreshed list
                    .map { notSyncedList ->
                        refreshList.toMutableList().apply { removeAll(notSyncedList) }
                    }
            }
            .flatMapCompletable { equipmentDao.updateAll(it) }
    }

    fun findEquipment(barcode: String): Single<Equipment> {
        return equipmentDao.getByBarcode(barcode)
    }

    fun updateEquipment(equipment: Equipment): Completable {
        val scannedAndSyncedEquipment = equipment.copy(scanState = ScanState.ScannedAndSynced)
        return equipmentDao.update(equipment.copy(scanState = ScanState.ScannedButNotSynced))
            .andThen(
                equipmentApi.update(
                    equipment.id,
                    equipmentResponseMapper.mapReverse(scannedAndSyncedEquipment)
                )
            )
            .andThen(Completable.defer { equipmentDao.update(scannedAndSyncedEquipment) })
    }
}
