package com.example.onmbarcode.presentation.station

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Station(val title: String, val scanCount: Int, val totalScanCount: Int): Parcelable