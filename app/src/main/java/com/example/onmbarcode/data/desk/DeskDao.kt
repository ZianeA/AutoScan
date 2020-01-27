package com.example.onmbarcode.data.desk

import androidx.room.*
import com.example.onmbarcode.data.equipment.EquipmentEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface DeskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAll(desk: List<DeskEntity>): Completable

    @Insert
    fun addAll(desk: List<DeskEntity>, equipments: List<EquipmentEntity>)

    @Query(
        """
        SELECT 
            COUNT(e.id) AS equipmentCount,
            SUM(CASE WHEN e.scanState = 'ScannedAndSynced' THEN 1 ELSE 0 END) AS syncedEquipmentCount,
            SUM(CASE WHEN e.scanState = 'ScannedButNotSynced' THEN 1 ELSE 0 END) AS notSyncedEquipmentCount,
            d.id,
            d.barcode,
            d.isScanned,
            d.scanDate
        FROM DeskEntity d
        LEFT JOIN EquipmentEntity e
            ON d.id = e.deskId
        WHERE d.isScanned = 1
        GROUP BY d.id, d.barcode, d.isScanned, d.scanDate
        ORDER BY d.scanDate Desc
        """
    )
    fun getScanned(): Single<List<DeskWithStatsEntity>>

    @Query(
        """
        SELECT 
            COUNT(e.id) AS equipmentCount,
            SUM(CASE WHEN e.scanState = 'ScannedAndSynced' THEN 1 ELSE 0 END) AS syncedEquipmentCount,
            SUM(CASE WHEN e.scanState = 'ScannedButNotSynced' THEN 1 ELSE 0 END) AS notSyncedEquipmentCount,
            d.id,
            d.barcode,
            d.isScanned,
            d.scanDate
        FROM DeskEntity d
        LEFT JOIN EquipmentEntity e
            ON d.id = e.deskId
        WHERE d.barcode=:barcode
        GROUP BY d.id, d.barcode, d.isScanned, d.scanDate
        """
    )
    fun getByBarcode(barcode: String): Maybe<DeskWithStatsEntity>

    @Query(
        """
        SELECT 
            COUNT(e.id) AS equipmentCount,
            SUM(CASE WHEN e.scanState = 'ScannedAndSynced' THEN 1 ELSE 0 END) AS syncedEquipmentCount,
            SUM(CASE WHEN e.scanState = 'ScannedButNotSynced' THEN 1 ELSE 0 END) AS notSyncedEquipmentCount,
            d.id,
            d.barcode,
            d.isScanned,
            d.scanDate
        FROM DeskEntity d
        LEFT JOIN EquipmentEntity e
            ON d.id = e.deskId
        WHERE d.id=:id
        GROUP BY d.id, d.barcode, d.isScanned, d.scanDate
        """
    )
    fun getById(id: Int): Single<DeskWithStatsEntity>

    @Update
    fun update(desk: DeskEntity): Completable

    @Query("SELECT CASE WHEN EXISTS(SELECT 1 from DeskEntity) THEN 0 ELSE 1 END")
    fun isEmpty(): Single<Boolean>

    @Query("DELETE FROM DeskEntity")
    fun deleteAll(): Completable
}