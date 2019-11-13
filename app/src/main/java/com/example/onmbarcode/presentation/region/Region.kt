package com.example.onmbarcode.presentation.region

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Region(val title: String, val scanCount: Int, val totalScanCount: Int): Parcelable