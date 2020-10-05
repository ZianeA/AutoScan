package com.meteoalgerie.autoscan.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey

data class User(val id: Int, val password: String)