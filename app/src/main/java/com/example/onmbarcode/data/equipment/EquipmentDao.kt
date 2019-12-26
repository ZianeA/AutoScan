package com.example.onmbarcode.data.equipment

import androidx.room.*
import com.example.onmbarcode.presentation.equipment.Equipment
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

    @Query("SELECT * FROM EquipmentEntity e WHERE e.deskId=:deskId")
    fun getByDesk(deskId: Int): Observable<List<EquipmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(equipments: List<EquipmentEntity>): Completable

    @Update
    fun update(equipment: EquipmentEntity): Completable

    @Query("SELECT * FROM EquipmentEntity e WHERE e.scanState=:scanState")
    fun getByScanState(scanState: Equipment.ScanState): Single<List<EquipmentEntity>>

    @Query("SELECT COUNT(*) FROM EquipmentEntity")
    fun getAllCount(): Single<Int>

    @Query("DELETE FROM EquipmentEntity")
    fun deleteAll(): Completable
}
