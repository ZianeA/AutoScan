package com.example.onmbarcode.presentation.desk

import io.reactivex.Completable
import io.reactivex.Single
import java.time.Year
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class DeskRepository @Inject constructor(private val local: DeskDao) {
    fun getScannedDesks(): Single<List<Desk>> {
        return local.getAll()
            .flatMap {
                if (it.isEmpty()) {
                    local.addAll(createDummyData())
                        .andThen(local.getScanned())
                } else {
                    local.getScanned()
                }
            }
    }

    fun findDesk(barcode: String) = local.getByBarcode(barcode)

    fun updateDesk(desk: Desk): Completable = local.update(desk)

    private fun createDummyData(dataCount: Int = 100): List<Desk> {
        val desks = mutableListOf<Desk>()
        for (i in 0..dataCount) {
            val deskBarcode = Random.nextInt(0, dataCount * 2)
            val totalScanCount = Random.nextInt(2, 20)
            val scanCount = Random.nextInt(0, totalScanCount)
            desks.add(
                Desk(
                    "@CNTM08",
                    false,
                    System.currentTimeMillis() - YEAR_IN_MILLIS,
                    0,
                    20
                )
            )
            desks.add(
                Desk(
                    "@CNTM$deskBarcode",
                    false,
                    System.currentTimeMillis() - YEAR_IN_MILLIS,
                    scanCount,
                    totalScanCount
                )
            )
        }

        return desks
    }

    //TODO remove
    companion object {
        private const val YEAR_IN_MILLIS = 31556952000
    }
}
