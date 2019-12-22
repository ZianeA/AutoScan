package com.example.onmbarcode.presentation.equipment

interface EquipmentView {
    fun displayEquipments(equipment: List<Equipment>)
    fun scrollToTop()
    fun clearBarcodeInputArea()
    fun displayEquipmentConditionChangedMessage()
    fun showErrorMessage()
    fun showUnknownBarcodeMessage()
    fun showEquipmentAlreadyScannedMessage()
    fun animateEquipment(equipmentId: Int)
    var isScrolling: Boolean
    fun displayProgressBarForEquipment(equipmentId: Int)
    fun hideProgressBarForEquipment(equipmentId: Int)
}
