package com.example.onmbarcode.data.desk

import android.util.Log
import com.example.onmbarcode.data.KeyValueStore
import com.example.onmbarcode.data.PreferencesIntStore.Companion.EQUIPMENT_COUNT_KEY
import com.example.onmbarcode.data.PreferencesIntStore.Companion.EQUIPMENT_DOWNLOADED_COUNT_KEY
import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.data.equipment.EquipmentDao
import com.example.onmbarcode.data.equipment.EquipmentEntity
import com.example.onmbarcode.data.equipment.EquipmentService
import com.example.onmbarcode.data.user.UserRepository
import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.equipment.Equipment
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ceil

@Singleton
class DeskRepository @Inject constructor(
    private val deskDao: DeskDao,
    private val equipmentDao: EquipmentDao,
    private val userRepository: UserRepository, //Should probably use userDao instead
    private val store: KeyValueStore<Int>,
    private val deskService: DeskService,
    private val equipmentService: EquipmentService,
    private val deskEntityMapper: Mapper<DeskWithStatsEntity, Desk>,
    private val equipmentEntityMapper: Mapper<EquipmentEntity, Equipment>,
    private val deskResponseMapper: Mapper<HashMap<*, *>, DeskEntity>,
    private val equipmentResponseMapper: Mapper<HashMap<*, *>, Equipment>
) {
    fun getScannedDesks(): Single<List<Desk>> {
        return deskDao.getScanned()
            .map { deskEntities -> deskEntities.map { deskEntityMapper.map(it) } }
    }

    fun isDatabaseEmpty(): Single<Boolean> = deskDao.isEmpty()

    fun downloadDatabase(): Observable<Int> {
        return userRepository.getUser()
            .toSingle()
            .flatMap {
                val downloadedEquipment = store.get(EQUIPMENT_DOWNLOADED_COUNT_KEY, 0)
                val allEquipment = store.get(EQUIPMENT_COUNT_KEY, 0)
                if(downloadedEquipment == 0 || downloadedEquipment != allEquipment){
                    if(downloadedEquipment == 0) deskDao.deleteAll()


                } else {

                }

                isDownloadFinished
            }
            .flatMapObservable { user ->
                deskService.getAll(user)
                    .map { list -> list.map { deskResponseMapper.map(it as HashMap<*, *>) } }
                    .flatMapCompletable { deskDao.addAll(it) }
                    .andThen(equipmentService.getEquipmentCount(user))
                    .flatMapObservable { count ->
                        store.put(EQUIPMENT_COUNT_KEY, count)
                        Observable.range(0, ceil(count.toDouble() / PAGE_SIZE).toInt() + 1)
                            .map {
                                object {
                                    val offset = it * PAGE_SIZE;
                                    val total = count
                                }
                            }
                    }
                    .doAfterNext {
                        Log.d(
                            "No Tag",
                            "Equipment: $it.offset - ${it.offset + PAGE_SIZE}"
                        )
                    }
                    .flatMap { holder ->
                        equipmentService.get(user, holder.offset, PAGE_SIZE)
                            .map { list -> list.map { equipmentResponseMapper.map(it as HashMap<*, *>) } }
                            .map { list -> list.map { equipmentEntityMapper.mapReverse(it) } }
                            .flatMapObservable {
                                equipmentDao.addAll(it)
                                    .andThen(Completable.defer {
                                        Completable.fromAction {
                                            store.put(
                                                EQUIPMENT_DOWNLOADED_COUNT_KEY,
                                                holder.offset + PAGE_SIZE
                                            )
                                        }
                                    })
                                    .andThen(Observable.just(holder.offset * 100 / holder.total))
                            }
                    }
            }
    }

    fun findDesk(barcode: String): Maybe<Desk> {
        return deskDao.getByBarcode(barcode)
            .map(deskEntityMapper::map)
    }

    fun updateDesk(desk: Desk): Completable {
        val deskEntity = deskEntityMapper.mapReverse(desk).deskEntity
        return deskDao.update(deskEntity)
    }

    fun deleteAllDesks(): Completable = deskDao.deleteAll()

    companion object {
        private const val PAGE_SIZE = 500
    }
}
