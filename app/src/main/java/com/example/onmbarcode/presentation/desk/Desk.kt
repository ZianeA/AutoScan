package com.example.onmbarcode.presentation.desk

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Desk(val barcode: Int, val scanCount: Int, val totalScanCount: Int) : Parcelable
