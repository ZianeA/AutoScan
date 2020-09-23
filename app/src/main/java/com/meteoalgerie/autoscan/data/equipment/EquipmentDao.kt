package com.meteoalgerie.autoscan.data.equipment

import androidx.room.*
import com.meteoalgerie.autoscan.presentation.equipment.Equipment
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface EquipmentDao {
    @Query("SELECT * FROM EquipmentEntity")
    fun getAll(): Single<List<EquipmentEntity>>

    @Query("SELECT * FROM EquipmentEntity e WHERE e.barcode=:barcode")
    fun getByBarcode(barcode: String): Maybe<EquipmentEntity>

    @Query("SELECT * FROM EquipmentEntity e WHERE e.deskId=:deskId ORDER BY e.scanDate Desc")
    fun getByDesk(deskId: Int): Observable<List<EquipmentEntity>>

    @Query(
        """SELECT * 
        FROM EquipmentEntity e 
        WHERE e.deskId=:deskId 
        AND e.scanState=:scanState 
        ORDER BY e.scanDate Desc"""
    )
    fun getByDeskAndScanState(
        deskId: Int,
        scanState: Equipment.ScanState
    ): Observable<List<EquipmentEntity>>

    @Query(
        """
        SELECT * 
        FROM EquipmentEntity e 
        WHERE e.deskId=:deskId 
        AND (e.scanState=:scanState1 OR e.scanState=:scanState2)
        ORDER BY e.scanDate Desc"""
    )
    fun getByDeskAndScanState(
        deskId: Int,
        scanState1: Equipment.ScanState,
        scanState2: Equipment.ScanState
    ): Observable<List<EquipmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(equipments: List<EquipmentEntity>): Completable

    @Update
    fun update(equipment: EquipmentEntity): Completable

    @Update
    fun updateAll(equipments: List<EquipmentEntity>): Completable

    @Query("SELECT * FROM EquipmentEntity e WHERE e.scanState=:scanState")
    fun getByScanState(scanState: Equipment.ScanState): Single<List<EquipmentEntity>>

    @Query("SELECT COUNT(*) FROM EquipmentEntity")
    fun getAllCount(): Single<Int>

    @Query("DELETE FROM EquipmentEntity")
    fun deleteAll(): Completable
}
