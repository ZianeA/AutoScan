package com.meteoalgerie.autoscan.data

import com.meteoalgerie.autoscan.data.desk.DeskEntity
import com.meteoalgerie.autoscan.data.desk.DeskResponseMapper
import com.meteoalgerie.autoscan.data.desk.DeskWithStatsEntity
import com.meteoalgerie.autoscan.data.desk.DeskWithStatsEntityMapper
import com.meteoalgerie.autoscan.data.equipment.EquipmentEntity
import com.meteoalgerie.autoscan.data.equipment.EquipmentEntityMapper
import com.meteoalgerie.autoscan.data.equipment.EquipmentResponseMapper
import com.meteoalgerie.autoscan.data.mapper.Mapper
import com.meteoalgerie.autoscan.data.user.UserEntity
import com.meteoalgerie.autoscan.data.user.UserEntityMapper
import com.meteoalgerie.autoscan.presentation.desk.Desk
import com.meteoalgerie.autoscan.presentation.equipment.Equipment
import com.meteoalgerie.autoscan.presentation.login.User
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {
    @Binds
    fun provideDeskWithStatsEntityMapper(mapper: DeskWithStatsEntityMapper): Mapper<DeskWithStatsEntity, Desk>

    @Binds
    fun provideEquipmentEntityMapper(mapper: EquipmentEntityMapper): Mapper<EquipmentEntity, Equipment>

    @Binds
    fun provideEquipmentResponseMapper(mapper: EquipmentResponseMapper): Mapper<HashMap<*, *>, EquipmentEntity>

    @Binds
    fun provideDeskResponseMapper(mapper: DeskResponseMapper): Mapper<HashMap<*, *>, DeskEntity>

    @Binds
    fun provideUserEntityMapper(mapper: UserEntityMapper): Mapper<UserEntity, User>

    @Binds
    fun providePreferences(preferencesStringStore: PreferencesStringStore): KeyValueStore<String>

    @Binds
    fun provideStringSetPreferences(preferences: PreferencesStringSetStore): KeyValueStore<Set<String>>
    fun provideStringPreferences(preferencesStringStore: PreferencesStringStore): KeyValueStore<String>

    @Binds
    fun provideIntPreferences(preferencesIntStore: PreferencesIntStore): KeyValueStore<Int>
}