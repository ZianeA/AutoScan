package com.meteoalgerie.autoscan.presentation.download

import com.meteoalgerie.autoscan.data.PreferenceStorage
import dagger.Reusable
import javax.inject.Inject

@Reusable
class IsDownloadCompleteUseCase @Inject constructor(private val storage: PreferenceStorage) {
    fun execute(): Boolean =
        storage.equipmentCount > 0 && storage.downloadedEquipmentCount >= storage.equipmentCount
}