package com.meteoalgerie.autoscan.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(@PrimaryKey val id: Int, val username: String, val password: String)