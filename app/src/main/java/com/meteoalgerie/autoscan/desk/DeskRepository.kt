package com.meteoalgerie.autoscan.desk

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeskRepository @Inject constructor(
    private val deskDao: DeskDao,
    private val deskEntityMapper: DeskWithStatsEntityMapper
) {
    fun getScannedDesks(): Observable<List<Desk>> {
        return deskDao.getScanned()
            .map { deskEntities -> deskEntities.map { deskEntityMapper.map(it) } }
    }

    fun findDesk(barcode: String): Single<Desk> {
        return deskDao.getByBarcode(barcode)
            .map(deskEntityMapper::map)
    }

    fun getDeskById(id: Int): Observable<Desk> {
        return deskDao.getById(id)
            .map(deskEntityMapper::map)
    }

    fun updateDesk(desk: Desk): Completable {
        val deskEntity = deskEntityMapper.mapReverse(desk).deskEntity
        return deskDao.update(deskEntity)
    }

    fun updateDesks(desks: List<Desk>): Completable {
        val deskEntities = desks.map { deskEntityMapper.mapReverse(it).deskEntity }
        return deskDao.updateAll(deskEntities)
    }
}
