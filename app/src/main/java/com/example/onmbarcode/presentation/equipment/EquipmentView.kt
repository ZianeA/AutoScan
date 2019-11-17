package com.example.onmbarcode.presentation.equipment

interface EquipmentView {
    fun displayEquipments(equipments: List<Equipment>)
    fun scrollToTop(currentIndex: Int)
}
