package com.meteoalgerie.autoscan.presentation.equipment

import com.meteoalgerie.autoscan.presentation.desk.Desk

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
    fun showEquipmentMovedMessage(equipmentId: Int)
    fun rebuildUi()
    fun showNetworkErrorMessage()
    fun showLoadingView()
    fun hideLoadingView()
}
