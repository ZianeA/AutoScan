package com.example.onmbarcode.data.equipment

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface EquipmentDao {
    @Query("SELECT * FROM EquipmentEntity")
    fun getAll(): Single<List<EquipmentEntity>>

    //TODO this should probably return a maybe
    @Query("SELECT * FROM EquipmentEntity e WHERE e.barcode=:barcode")
    fun getByBarcode(barcode: String): Single<EquipmentEntity>

    //TODO add index for deskBarcode
    @Query("SELECT * FROM EquipmentEntity e WHERE e.deskBarcode=:deskBarcode")
    fun getByDesk(deskBarcode: String): Single<List<EquipmentEntity>>

    //TODO is this right? Probably not. I think I was using to insert dummy data without conflict
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(equipments: List<EquipmentEntity>): Completable

    @Update
    fun update(equipment: EquipmentEntity): Completable
}
