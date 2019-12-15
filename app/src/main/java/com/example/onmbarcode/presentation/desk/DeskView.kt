package com.example.onmbarcode.presentation.desk

interface DeskView {
    fun displayDesks(desks: List<DeskUi>)
    fun displayEquipmentsScreen(desk: DeskUi)
    fun displayUnknownBarcodeMessage()
    fun displayGenericErrorMessage()
    fun clearBarcodeInputArea()
    fun disableBarcodeInput()
    fun enableBarcodeInput()
    fun displayLoginScreen()
}
