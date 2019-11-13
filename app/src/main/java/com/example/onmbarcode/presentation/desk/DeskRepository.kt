package com.example.onmbarcode.presentation.desk

import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class DeskRepository @Inject constructor() {
    fun getDesks(): Single<List<Desk>> {
        return Single.just(createDummyData())
    }

    private fun createDummyData(dataCount: Int = 100): List<Desk> {
        val desks = mutableListOf<Desk>()
        for (i in 0..dataCount) {
            val deskBarcode = Random.nextInt(2000, 9999)
            val totalScanCount = Random.nextInt(2, 20)
            val scanCount = Random.nextInt(0, totalScanCount)
            desks.add(Desk(deskBarcode, scanCount, totalScanCount))
        }

        return desks
    }
}
