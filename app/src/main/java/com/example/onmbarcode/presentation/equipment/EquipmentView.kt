package com.example.onmbarcode.presentation.equipment

interface EquipmentView {
    fun displayEquipments()
    fun displayEquipmentsDelayed()
    fun scrollToTop()
    fun scrollToTopAndDisplayEquipments()
    fun clearBarcodeInputArea()
    fun displayEquipmentConditionChangedMessage()
    var equipments: List<Equipment>
    var equipmentToAnimate: Int
}
