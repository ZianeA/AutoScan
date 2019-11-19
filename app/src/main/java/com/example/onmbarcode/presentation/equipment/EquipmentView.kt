package com.example.onmbarcode.presentation.equipment

interface EquipmentView {
    fun displayEquipments(equipments: List<Equipment>)
    fun scrollToTop()
    fun displayEquipmentStatePicker(currentState: Equipment.EquipmentState)
    fun animateEquipment(equipmentBarcode: Long)
    fun smoothScrollToTop()
}
