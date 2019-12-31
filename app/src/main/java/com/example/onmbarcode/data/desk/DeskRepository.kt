package com.example.onmbarcode.data.desk

import android.util.Log
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

    fun downloadDatabase(): Completable {
        return userRepository.getUser()
            .toSingle()
            .flatMapCompletable { user ->
                deskService.getAll(user)
                    .map { list -> list.map { deskResponseMapper.map(it as HashMap<*, *>) } }
                    .flatMapCompletable { deskDao.addAll(it) }
                    .andThen(equipmentService.getEquipmentCount(user))
                    .flatMapObservable { count ->
                        Observable.range(0, ceil(count.toDouble() / PAGE_SIZE).toInt() + 1)
                    }
                    .map { it * PAGE_SIZE }
                    .doAfterNext { Log.d("No Tag", "Equipment: $it - ${it + PAGE_SIZE}") }
                    .flatMapSingle { equipmentService.get(user, it, PAGE_SIZE) }
                    .map { list -> list.map { equipmentResponseMapper.map(it as HashMap<*, *>) } }
                    .map { list -> list.map { equipmentEntityMapper.mapReverse(it) } }
                    .flatMapCompletable { equipmentDao.addAll(it) }
            }
    }

    fun findDesk(barcode: String): Maybe<Desk> {
        return deskDao.getByBarcode(barcode)
            .map(deskEntityMapper::map)
    }

    fun getDeskById(id: Int): Single<Desk> {
        return deskDao.getById(id)
            .map(deskEntityMapper::map)
    }

    fun updateDesk(desk: Desk): Completable {
        val deskEntity = deskEntityMapper.mapReverse(desk).deskEntity
        return deskDao.update(deskEntity)
    }

    companion object {
        private const val PAGE_SIZE = 500
    }
}
