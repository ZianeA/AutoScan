package com.example.onmbarcode.presentation.equipment

interface EquipmentView {
    fun displayEquipments()
    fun displayEquipmentsDelayed()
    fun scrollToTop()
    fun scrollToTopAndDisplayEquipments()
    fun clearBarcodeInputArea()
    fun displayEquipmentConditionChangedMessage()
    fun showErrorMessage()

    var equipments: List<Equipment>
    var equipmentToAnimate: String
}
