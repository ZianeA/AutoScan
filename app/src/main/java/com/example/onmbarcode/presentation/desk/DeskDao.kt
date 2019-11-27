package com.example.onmbarcode.presentation.desk

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface DeskDao {
    //TODO update conflict strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(desk: List<Desk>): Completable

    @Query("SELECT * FROM Desk")
    fun getAll(): Single<List<Desk>>

    @Query("SELECT * FROM Desk e WHERE e.isScanned = 1 ORDER BY e.scanDate")
    fun getScanned(): Single<List<Desk>>

    @Query("SELECT * FROM Desk d WHERE d.barcode=:barcode")
    fun getByBarcode(barcode: String): Single<Desk>

    @Update
    fun update(desk: Desk): Completable
}