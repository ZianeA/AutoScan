package com.example.onmbarcode.presentation.equipment

import androidx.room.*
import com.example.onmbarcode.presentation.equipment.Equipment
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface EquipmentDao {
    @Query("SELECT * FROM Equipment")
    fun getAll(): Single<List<Equipment>>

    //TODO this should probably return a maybe
    @Query("SELECT * FROM Equipment e WHERE e.barcode=:barcode")
    fun getByBarcode(barcode: Int): Single<Equipment>

    //TODO is this right? Probably not. I think I was using to insert dummy data without conflict
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(equipments: List<Equipment>): Completable

    @Update
    fun update(equipment: Equipment): Completable
}
