package com.meteoalgerie.autoscan.data.user

import com.meteoalgerie.autoscan.data.mapper.Mapper
import com.meteoalgerie.autoscan.presentation.login.User
import dagger.Reusable
import javax.inject.Inject

@Reusable
class UserEntityMapper @Inject constructor() : Mapper<UserEntity, User> {
    override fun map(model: UserEntity): User {
        return model.run { User(id, username, password) }
    }

    override fun mapReverse(model: User): UserEntity {
        return model.run { UserEntity(id, username, password) }
    }
}