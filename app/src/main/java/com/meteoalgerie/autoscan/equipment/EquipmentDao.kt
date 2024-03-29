package com.meteoalgerie.autoscan.equipment

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface EquipmentDao {
    @Query("SELECT * FROM EquipmentEntity")
    fun getAll(): Single<List<Equipment>>

    @Query("SELECT * FROM EquipmentEntity WHERE barcode=:barcode")
    fun getByBarcode(barcode: String): Single<Equipment>

    @Query("SELECT * FROM EquipmentEntity WHERE deskId=:deskId ORDER BY scanDate Desc")
    fun getByDesk(deskId: Int): Observable<List<Equipment>>

    @Query(
        """SELECT * 
        FROM EquipmentEntity
        WHERE deskId=:deskId 
        AND scanState=:scanState 
        ORDER BY scanDate Desc"""
    )
    fun getByDeskAndScanState(
        deskId: Int,
        scanState: Equipment.ScanState
    ): Observable<List<Equipment>>

    @Query(
        """
        SELECT * 
        FROM EquipmentEntity 
        WHERE deskId=:deskId 
        AND (scanState=:scanState1 OR scanState=:scanState2)
        ORDER BY scanDate Desc"""
    )
    fun getByDeskAndScanState(
        deskId: Int,
        scanState1: Equipment.ScanState,
        scanState2: Equipment.ScanState
    ): Observable<List<Equipment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(equipments: List<Equipment>): Completable

    @Update
    fun update(equipment: Equipment): Completable

    @Update
    fun updateAll(equipments: List<Equipment>): Completable

    @Query("SELECT * FROM EquipmentEntity WHERE scanState=:scanState")
    fun getByScanState(scanState: Equipment.ScanState): Single<List<Equipment>>

    @Query("SELECT COUNT(*) FROM EquipmentEntity")
    fun getAllCount(): Single<Int>

    @Query("DELETE FROM EquipmentEntity")
    fun deleteAll(): Completable
}
