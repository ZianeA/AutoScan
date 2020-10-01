package com.meteoalgerie.autoscan.presentation.download

import android.util.Log
import com.meteoalgerie.autoscan.data.PreferenceStorage
import com.meteoalgerie.autoscan.data.desk.*
import com.meteoalgerie.autoscan.data.equipment.Equipment
import com.meteoalgerie.autoscan.data.equipment.EquipmentDao
import com.meteoalgerie.autoscan.data.equipment.EquipmentService
import com.meteoalgerie.autoscan.data.mapper.Mapper
import com.meteoalgerie.autoscan.data.user.User
import com.meteoalgerie.autoscan.data.user.UserDao
import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import kotlin.math.ceil

@Reusable
class DownloadDataUseCase @Inject constructor(
    private val deskDao: DeskDao,
    private val equipmentDao: EquipmentDao,
    private val userDao: UserDao,
    private val storage: PreferenceStorage,
    private val deskService: DeskService,
    private val equipmentService: EquipmentService,
    private val deskResponseMapper: Mapper<HashMap<*, *>, DeskEntity>,
    private val equipmentResponseMapper: Mapper<HashMap<*, *>, Equipment>
) {

    companion object {
        private const val PAGE_SIZE = 500
    }

    fun execute(): Observable<Int> = if (!isDownloadComplete()) download() else Observable.empty()

    private fun download(): Observable<Int> {
        return userDao.get().flatMapObservable { user ->
            Observable.concat(
                downloadDesks(user).takeWhile { storage.downloadedEquipmentCount == 0 },
                downloadEquipments(user)
            )
        }
    }

    private fun downloadDesks(user: User): Observable<Int> {
        return deskService.getAll(user)
            .map { list -> list.map { deskResponseMapper.map(it as HashMap<*, *>) } }
            .flatMapObservable { deskDao.addAll(it).toObservable<Int>() }
    }

    private fun downloadEquipments(user: User): Observable<Int> {
        return equipmentService.getEquipmentCount(user)
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
                equipmentService.get(user, offset, PAGE_SIZE)
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

    private fun isDownloadComplete() =
        storage.equipmentCount > 0 && storage.downloadedEquipmentCount >= storage.equipmentCount
}