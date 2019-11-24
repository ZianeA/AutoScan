package com.example.onmbarcode.presentation.equipment

import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class EquipmentRepository @Inject constructor(private val local: EquipmentDao) {
    fun getEquipments(deskId: Int): Single<List<Equipment>> {
        return local.getAll()
            .flatMap {
                if (it.isEmpty()) {
                    local.addAll(createDummyData(100))
                        .andThen(local.getAll())
                } else {
                    Single.just(it)
                }
            }
    }

    fun findEquipment(barcode: Int): Single<Equipment> {
        return local.getByBarcode(barcode)
    }

    fun updateEquipment(equipment: Equipment): Completable {
        return local.update(equipment)
    }

    private fun createDummyData(dataCount: Int = 20): List<Equipment> {
        val equipments = mutableListOf<Equipment>()
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
                Equipment(
                    barcode,
                    type,
                    Equipment.ScanState.NotScanned,
                    equipmentState,
                    System.currentTimeMillis()
                )
            )
        }

        return equipments
    }
}
