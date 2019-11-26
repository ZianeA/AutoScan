package com.example.onmbarcode.presentation.equipment

interface EquipmentView {
    fun displayEquipments(equipments: List<Equipment>, equipmentToAnimate: Int = -1)
    fun displayEquipmentsDelayed(equipments: List<Equipment>, equipmentToAnimate: Int)
    fun scrollToTop()
    fun scrollToTopAndDisplayEquipments(equipments: List<Equipment>)
    fun clearBarcodeInputArea()
    fun displayEquipmentConditionChangedMessage()
    fun getEquipments() : List<Equipment>
}
