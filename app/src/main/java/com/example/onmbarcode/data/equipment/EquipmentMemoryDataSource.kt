package com.example.onmbarcode.data.equipment

import com.example.onmbarcode.presentation.equipment.Equipment
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EquipmentMemoryDataSource @Inject constructor() {
    private val equipment = mutableListOf<Equipment>()

    /*fun getAllEquipmentForDesk(): Single<> {
        return equipment
    }*/
}