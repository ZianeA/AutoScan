package com.example.onmbarcode.data.desk

import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.data.equipment.EquipmentDao
import com.example.onmbarcode.data.equipment.EquipmentEntity
import com.example.onmbarcode.data.equipment.EquipmentResponseMapper
import com.example.onmbarcode.data.equipment.EquipmentService
import com.example.onmbarcode.data.user.UserDao
import com.example.onmbarcode.data.user.UserRepository
import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.equipment.Equipment
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class DeskRepository @Inject constructor(
    private val deskDao: DeskDao,
    private val userRepository: UserRepository, //Should probably use userDao instead
    private val deskService: DeskService,
    private val equipmentService: EquipmentService,
    private val deskEntityMapper: Mapper<DeskWithEquipmentsEntity, Desk>,
    private val equipmentEntityMapper: Mapper<EquipmentEntity, Equipment>,
    private val deskResponseMapper: Mapper<HashMap<*, *>, DeskEntity>,
    private val equipmentResponseMapper: Mapper<HashMap<*, *>, Equipment>
) {
    fun getScannedDesks(): Single<List<Desk>> {
        return deskDao.getAll()
            .flatMap {
                if (it.isEmpty()) {
                    Single.just(emptyList())
                } else {
                    deskDao.getScanned()
                }
            }
            .map { deskEntities -> deskEntities.map { deskEntityMapper.map(it) } }
    }

    // If local cache is empty, fetch desks from server. Save the fetched desks to the local cache.
    // Finally, find the desk in the local cache.
    fun findDesk(barcode: String): Maybe<Desk> {
        return deskDao.isEmpty()
            .flatMapMaybe { isLocalCacheEmpty ->
                if (isLocalCacheEmpty) {
                    userRepository.getUser()
                        .toSingle() // Should error if there's no user.
                        .flatMapMaybe { user ->
                            deskService.getAll(user)
                                .map { list -> list.map { deskResponseMapper.map(it as HashMap<*, *>) } }
                                .flatMapCompletable { desks ->
                                    equipmentService.getAll(user)
                                        .map { list -> list.map { equipmentResponseMapper.map(it as HashMap<*, *>) } }
                                        .map { list ->
                                            list.map {
                                                equipmentEntityMapper.mapReverse(
                                                    it
                                                )
                                            }
                                        }
                                        .flatMapCompletable { equipments ->
                                            Completable.fromAction {
                                                deskDao.addAll(
                                                    desks,
                                                    equipments
                                                )
                                            }
                                        }
                                }
                                .andThen(deskDao.getByBarcode(barcode))
                                .map(deskEntityMapper::map)
                        }
                } else {
                    deskDao.getByBarcode(barcode)
                        .map(deskEntityMapper::map)
                }
            }
    }

    fun updateDesk(desk: Desk): Completable {
        val deskEntity = deskEntityMapper.mapReverse(desk).deskEntity
        return deskDao.update(deskEntity)
    }

    /*private fun createDummyData(dataCount: Int = 100): List<DeskEntity> {
        val desks = mutableListOf<DeskEntity>()
        for (i in 0..dataCount) {
            val deskId = Random.nextInt(0, dataCount * 2)
            val totalScanCount = Random.nextInt(2, 20)
            val scanCount = Random.nextInt(0, totalScanCount)
            desks.add(
                DeskEntity(
                    "CNTM08",
                    false,
                    System.currentTimeSeconds() - YEAR_IN_MILLIS
                )
            )
            desks.add(
                DeskEntity(
                    "CNTM$deskId",
                    false,
                    System.currentTimeSeconds() - YEAR_IN_MILLIS
                )
            )
        }

        return desks
    }*/

    //TODO remove
    companion object {
        private const val YEAR_IN_MILLIS = 31556952000
    }
}
