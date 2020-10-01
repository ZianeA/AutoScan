package com.meteoalgerie.autoscan.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserEntity")
data class User(@PrimaryKey val id: Int, val username: String, val password: String)