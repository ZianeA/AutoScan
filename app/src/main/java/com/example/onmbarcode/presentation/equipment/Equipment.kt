package com.example.onmbarcode.presentation.equipment

data class Equipment(
    val barcode: Int,
    val type: String,
    val isScanned: Boolean,
    val state: EquipmentState
) {
    enum class EquipmentState {
        GOOD, AVERAGE, BAD;

        companion object {
            private val values = values();
            fun getByValue(value: Int) = values.first { it.ordinal == value }
        }
    }
}
