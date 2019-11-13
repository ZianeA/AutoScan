package com.example.onmbarcode.presentation.equipment

import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class EquipmentRepository @Inject constructor() {
    fun getEquipments(deskId: Int): Single<List<Equipment>> {
        return Single.just(createDummyData())
    }

    private fun createDummyData(dataCount: Int = 100): List<Equipment> {
        val equipments = mutableListOf<Equipment>()
        val equipmentTypes = listOf("écran", "clavier", "souris", "chaise", "imprimante", "bureau")
        for (i in 0..dataCount) {
            val barcode = Random.nextInt(2000, 9999)
            val type = equipmentTypes[Random.nextInt(0, equipmentTypes.size - 1)]
            equipments.add(Equipment(barcode, type))
        }

        return equipments
    }
}
