package com.example.onmbarcode.presentation.equipment

import com.example.onmbarcode.presentation.desk.Desk

interface EquipmentView {
    fun displayEquipments(desk: Desk, equipment: List<Equipment>, tags: Set<String>)
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
    fun showEquipmentMovedMessage()
}
