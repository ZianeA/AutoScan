package com.meteoalgerie.autoscan.data.desk

import com.meteoalgerie.autoscan.data.PreferenceStorage
import com.meteoalgerie.autoscan.data.mapper.Mapper
import com.meteoalgerie.autoscan.presentation.desk.Desk
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeskRepository @Inject constructor(
    private val deskDao: DeskDao,
    private val deskEntityMapper: Mapper<DeskWithStatsEntity, Desk>
) {
    fun getScannedDesks(): Observable<List<Desk>> {
        return deskDao.getScanned()
            .map { deskEntities -> deskEntities.map { deskEntityMapper.map(it) } }
    }

    fun findDesk(barcode: String): Single<Desk> {
        return deskDao.getByBarcode(barcode)
            .map(deskEntityMapper::map)
    }

    fun getDeskById(id: Int): Single<Desk> {
        return deskDao.getById(id)
            .map(deskEntityMapper::map)
    }

    fun updateDesk(desk: Desk): Completable {
        val deskEntity = deskEntityMapper.mapReverse(desk).deskEntity
        return deskDao.update(deskEntity)
    }
}
