package com.example.onmbarcode.data.user

import androidx.room.*
import com.example.onmbarcode.presentation.login.User
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(user: UserEntity): Completable

    @Query("DELETE FROM UserEntity")
    fun removeAll(): Completable

    /*@Query("SELECT * FROM UserEntity u WHERE u.id=:uid")
    fun get(uid: Int): Single<UserEntity>*/

    @Query("SELECT * FROM UserEntity")
    fun getAll(): Maybe<List<UserEntity>>
}