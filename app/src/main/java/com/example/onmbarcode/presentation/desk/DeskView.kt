package com.example.onmbarcode.presentation.desk

interface DeskView {
    fun displayDesks(desks: List<Desk>)
    fun displayEquipmentsScreen(desk: Desk)
    fun displayUnknownBarcodeMessage()
    fun displayGenericErrorMessage()
    fun clearBarcodeInputArea()
    fun disableBarcodeInput()
    fun enableBarcodeInput()
    fun displayLoginScreen()
    fun setDownloadProgress(percentage: Int)
    fun displayDownloadViews()
    fun hideDownloadViews()
}
