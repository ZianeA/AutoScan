package com.example.onmbarcode.presentation.equipment

interface EquipmentView {
    fun displayEquipments(equipments: List<Equipment>, equipmentToAnimate: Int = -1)
    fun scrollToTop()
    fun displayEquipmentConditionPicker(currentCondition: Equipment.EquipmentCondition)
    fun smoothScrollToTop()
    fun clearBarcodeInputArea()
}
