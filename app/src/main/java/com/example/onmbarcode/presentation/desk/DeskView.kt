package com.example.onmbarcode.presentation.desk

interface DeskView {
    fun displayDesks(desks: List<DeskUi>)
    fun displayEquipmentsScreen(desk: DeskUi)
    fun showUnknownBarcodeMessage()
}
