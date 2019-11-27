package com.example.onmbarcode.data.desk

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface DeskDao {
    //TODO update conflict strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(desk: List<DeskEntity>): Completable

    @Query("SELECT * FROM DeskEntity")
    fun getAll(): Single<List<DeskEntity>>

    @Query("SELECT * FROM DeskEntity e WHERE e.isScanned = 1 ORDER BY e.scanDate")
    fun getScanned(): Single<List<DeskEntity>>

    @Query("SELECT * FROM DeskEntity d WHERE d.barcode=:barcode")
    fun getByBarcode(barcode: String): Single<DeskEntity>

    @Update
    fun update(desk: DeskEntity): Completable
}