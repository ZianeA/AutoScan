package com.example.onmbarcode.data.equipment

import com.example.onmbarcode.data.Mapper
import com.example.onmbarcode.presentation.equipment.Equipment
import io.reactivex.Completable
import io.reactivex.Single
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class EquipmentRepository @Inject constructor(
    private val local: EquipmentDao,
    private val equipmentEntityMapper: Mapper<EquipmentEntity, Equipment>
) {
    fun getEquipments(deskId: String): Single<List<Equipment>> {
        return local.getAll()
            .flatMap {
                if (it.isEmpty()) {
                    local.addAll(createDummyData(100, deskId))
                        .andThen(local.getAll())
                } else {
                    Single.just(it)
                }
            }.map { equipmentEntities -> equipmentEntities.map(equipmentEntityMapper::map) }
    }

    fun findEquipment(barcode: String): Single<Equipment> {
        return local.getByBarcode(barcode)
            .map(equipmentEntityMapper::map)
    }

    //TODO be careful with equipment scan state if network fails
    // it makes sense to update network before database
    fun updateEquipment(equipment: Equipment): Completable {
        return Single.just(Random.nextBoolean())
            .flatMapCompletable {
                if (it) {
                    local.update(equipmentEntityMapper.mapReverse(equipment))
                } else {
                    throw IOException()
                }
            }
//        return local.update(equipmentEntityMapper.mapReverse(equipment))
    }

    private fun createDummyData(dataCount: Int = 20, deskId: String): List<EquipmentEntity> {
        val equipments = mutableListOf<EquipmentEntity>()
        val equipmentTypes = listOf("écran", "clavier", "souris", "chaise", "imprimante", "bureau")
        for (i in 0..dataCount) {
            val barcode = when (i) {
                10 -> 25113
                20 -> 30317
                25 -> 19955
                41 -> 26137
                else -> Random.nextInt(10000, 99999)

            }

            val type = equipmentTypes[Random.nextInt(0, equipmentTypes.size - 1)]
            val equipmentState = Equipment.EquipmentCondition.values().toList().shuffled().first()
            equipments.add(
                EquipmentEntity(
                    barcode.toString(),
                    type,
                    Equipment.ScanState.NotScanned,
                    equipmentState,
                    System.currentTimeMillis(),
                    deskId
                )
            )
        }

        return equipments
    }
}
