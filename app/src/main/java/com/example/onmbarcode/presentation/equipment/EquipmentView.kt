package com.example.onmbarcode.presentation.equipment

interface EquipmentView {
    fun displayEquipments(equipments: List<Equipment>, equipmentToAnimate: Int = -1)
    fun scrollToTop()
    fun displayEquipmentStatePicker(currentState: Equipment.EquipmentState)
    fun animateEquipment(barcode: Int)
    fun smoothScrollToTop()
}
