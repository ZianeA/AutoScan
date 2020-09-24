package com.meteoalgerie.autoscan.data

import com.meteoalgerie.autoscan.data.desk.DeskEntity
import com.meteoalgerie.autoscan.data.desk.DeskResponseMapper
import com.meteoalgerie.autoscan.data.desk.DeskWithStatsEntity
import com.meteoalgerie.autoscan.data.desk.DeskWithStatsEntityMapper
import com.meteoalgerie.autoscan.data.equipment.Equipment
import com.meteoalgerie.autoscan.data.equipment.EquipmentMapper
import com.meteoalgerie.autoscan.data.equipment.EquipmentResponseMapper
import com.meteoalgerie.autoscan.data.mapper.Mapper
import com.meteoalgerie.autoscan.data.user.UserEntity
import com.meteoalgerie.autoscan.data.user.UserEntityMapper
import com.meteoalgerie.autoscan.presentation.desk.Desk
import com.meteoalgerie.autoscan.presentation.login.User
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {
    @Binds
    fun provideDeskWithStatsEntityMapper(mapper: DeskWithStatsEntityMapper): Mapper<DeskWithStatsEntity, Desk>

    @Binds
    fun provideEquipmentEntityMapper(mapper: EquipmentMapper): Mapper<Equipment, Equipment>

    @Binds
    fun provideEquipmentResponseMapper(mapper: EquipmentResponseMapper): Mapper<HashMap<*, *>, Equipment>

    @Binds
    fun provideDeskResponseMapper(mapper: DeskResponseMapper): Mapper<HashMap<*, *>, DeskEntity>

    @Binds
    fun provideUserEntityMapper(mapper: UserEntityMapper): Mapper<UserEntity, User>
}