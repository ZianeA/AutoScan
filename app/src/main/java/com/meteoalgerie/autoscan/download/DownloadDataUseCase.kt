package com.meteoalgerie.autoscan.download

import com.meteoalgerie.autoscan.common.database.PreferenceStorage
import com.meteoalgerie.autoscan.desk.DeskDao
import com.meteoalgerie.autoscan.desk.DeskResponseMapper
import com.meteoalgerie.autoscan.desk.DeskApi
import com.meteoalgerie.autoscan.equipment.EquipmentDao
import com.meteoalgerie.autoscan.equipment.EquipmentResponseMapper
import com.meteoalgerie.autoscan.equipment.EquipmentApi
import dagger.Reusable
import io.reactivex.Observable
import javax.inject.Inject
import kotlin.math.ceil

@Reusable
class DownloadDataUseCase @Inject constructor(
    private val deskDao: DeskDao,
    private val equipmentDao: EquipmentDao,
    private val storage: PreferenceStorage,
    private val isDownloadCompleteUseCase: IsDownloadCompleteUseCase,
    private val deskApi: DeskApi,
    private val equipmentApi: EquipmentApi,
    private val deskResponseMapper: DeskResponseMapper,
    private val equipmentResponseMapper: EquipmentResponseMapper
) {

    companion object {
        private const val PAGE_SIZE = 500
    }

    fun execute(): Observable<Int> =
        if (!isDownloadCompleteUseCase.execute()) download() else Observable.empty()

    private fun download(): Observable<Int> {
        return Observable.concat(
            downloadDesks().takeWhile { storage.downloadedEquipmentCount == 0 },
            downloadEquipments()
        )
    }

    private fun downloadDesks(): Observable<Int> {
        return deskApi.getAll()
            .map { list -> list.map { deskResponseMapper.map(it as HashMap<*, *>) } }
            .flatMapObservable { deskDao.addAll(it).toObservable<Int>() }
    }

    private fun downloadEquipments(): Observable<Int> {
        return equipmentApi.getEquipmentCount()
            .flatMapObservable { count ->
                storage.equipmentCount = count
                val rangeStart =
                    ceil(storage.downloadedEquipmentCount.toDouble() / PAGE_SIZE).toInt()
                val rangeCount =
                    ceil(count.toDouble() / PAGE_SIZE).toInt() - rangeStart

                Observable.range(rangeStart, rangeCount)
                    .map { it * PAGE_SIZE }
            }
            .concatMap { offset ->
                equipmentApi.get(offset, PAGE_SIZE)
                    .map { list -> list.map { equipmentResponseMapper.map(it as HashMap<*, *>) } }
                    .flatMapObservable {
                        equipmentDao.addAll(it).andThen(
                            Observable.fromCallable {
                                storage.downloadedEquipmentCount += it.size

                                //calculate download percentage
                                ceil(offset * 100f / storage.equipmentCount).toInt()
                            }
                        )
                    }
            }
    }
}