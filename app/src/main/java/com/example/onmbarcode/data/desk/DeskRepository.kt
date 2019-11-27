package com.example.onmbarcode.data.desk

import com.example.onmbarcode.data.Mapper
import com.example.onmbarcode.presentation.desk.Desk
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class DeskRepository @Inject constructor(
    private val local: DeskDao,
    private val deskEntityMapper: Mapper<DeskWithEquipmentsEntity, Desk>
) {
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
            .map { deskEntities -> deskEntities.map { deskEntityMapper.map(it) } }
    }

    fun findDesk(barcode: String): Single<Desk> {
        return local.getByBarcode(barcode)
            .map(deskEntityMapper::map)
    }

    fun updateDesk(desk: Desk): Completable {
        val deskEntity = deskEntityMapper.mapReverse(desk).deskEntity
        return local.update(deskEntity)
    }

    private fun createDummyData(dataCount: Int = 100): List<DeskEntity> {
        val desks = mutableListOf<DeskEntity>()
        for (i in 0..dataCount) {
            val deskBarcode = Random.nextInt(0, dataCount * 2)
            val totalScanCount = Random.nextInt(2, 20)
            val scanCount = Random.nextInt(0, totalScanCount)
            desks.add(
                DeskEntity(
                    "@CNTM08",
                    false,
                    System.currentTimeMillis() - YEAR_IN_MILLIS
                )
            )
            desks.add(
                DeskEntity(
                    "@CNTM$deskBarcode",
                    false,
                    System.currentTimeMillis() - YEAR_IN_MILLIS
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
