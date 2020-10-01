package com.meteoalgerie.autoscan.presentation.desk

interface DeskView {
    fun displayDesks(desks: List<Desk>)
    fun displayEquipmentsScreen(desk: Desk)
    fun displayUnknownBarcodeMessage()
    fun displayGenericErrorMessage()
    fun clearBarcodeInputArea()
    fun disableBarcodeInput()
    fun enableBarcodeInput()
    fun displayLoginScreen()
    fun displayScanDeskMessage()
}
