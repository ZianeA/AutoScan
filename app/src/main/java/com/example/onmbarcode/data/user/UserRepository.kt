package com.example.onmbarcode.data.user

import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.presentation.login.User
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val mapper: Mapper<UserEntity, User>
) {
    fun addUser(user: User): Completable {
        return userDao.removeAll()
            .andThen(userDao.add(mapper.mapReverse(user)))
    }

    fun removeUser(): Completable {
        return userDao.removeAll()
    }

    // TODO change return type to single
    fun getUser(): Single<User> {
        return userDao.getAll()
            .map { it.single() } //There should be only one user in the database
            .map(mapper::map)
    }

    /*fun getAllUsers(): Single<List<User>> {
        return userDao.getAll().map { it.map(mapper::map) }
    }*/
}