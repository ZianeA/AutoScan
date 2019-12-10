package com.example.onmbarcode.data.desk

import androidx.room.*
import com.example.onmbarcode.data.equipment.EquipmentEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface DeskDao {
    //TODO update conflict strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(desk: List<DeskEntity>): Completable

    @Insert
    fun addAll(desk: List<DeskEntity>, equipments: List<EquipmentEntity>)

    @Query("SELECT * FROM DeskEntity")
    fun getAll(): Single<List<DeskWithEquipmentsEntity>>

    @Query("SELECT * FROM DeskEntity e WHERE e.isScanned = 1 ORDER BY e.scanDate")
    fun getScanned(): Single<List<DeskWithEquipmentsEntity>>

    @Query("SELECT * FROM DeskEntity d WHERE d.barcode=:barcode")
    fun getByBarcode(barcode: String): Maybe<DeskWithEquipmentsEntity>

    @Update
    fun update(desk: DeskEntity): Completable

    @Query("SELECT CASE WHEN EXISTS(SELECT 1 from DeskEntity) THEN 0 ELSE 1 END")
    fun isEmpty(): Single<Boolean>
}