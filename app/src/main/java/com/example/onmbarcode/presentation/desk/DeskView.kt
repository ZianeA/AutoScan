package com.example.onmbarcode.presentation.desk

interface DeskView {
    fun displayDesks(desks: List<DeskUi>)
    fun displayEquipmentsScreen(desk: DeskUi)
    fun showUnknownBarcodeMessage()
    fun showErrorMessage()
    fun clearBarcodeInputArea()
    fun disableBarcodeInput()
    fun enableBarcodeInput()
}
