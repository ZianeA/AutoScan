package com.meteoalgerie.autoscan.data.user

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(user: User): Completable

    @Query("DELETE FROM UserEntity")
    fun delete(): Completable

    /*@Query("SELECT * FROM UserEntity u WHERE u.id=:uid")
    fun get(uid: Int): Single<UserEntity>*/

    @Query("SELECT * FROM UserEntity")
    fun get(): Single<User>
}