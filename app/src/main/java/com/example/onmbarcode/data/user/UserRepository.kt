package com.example.onmbarcode.data.user

import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.presentation.login.User
import io.reactivex.Completable
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

    /*fun removeUser(user: User): Completable {
        return userDao.remove(mapper.mapReverse(user))
    }*/

    fun getUserById(uid: Int): Single<User> {
        return userDao.get(uid).map(mapper::map)
    }

    /*fun getAllUsers(): Single<List<User>> {
        return userDao.getAll().map { it.map(mapper::map) }
    }*/
}