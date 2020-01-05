package com.example.onmbarcode.data

import com.example.onmbarcode.data.desk.DeskEntity
import com.example.onmbarcode.data.desk.DeskResponseMapper
import com.example.onmbarcode.data.desk.DeskWithStatsEntity
import com.example.onmbarcode.data.desk.DeskWithStatsEntityMapper
import com.example.onmbarcode.data.equipment.EquipmentEntity
import com.example.onmbarcode.data.equipment.EquipmentEntityMapper
import com.example.onmbarcode.data.equipment.EquipmentResponseMapper
import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.data.user.UserEntity
import com.example.onmbarcode.data.user.UserEntityMapper
import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.equipment.Equipment
import com.example.onmbarcode.presentation.login.User
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {
    @Binds
    fun provideDeskWithStatsEntityMapper(mapper: DeskWithStatsEntityMapper): Mapper<DeskWithStatsEntity, Desk>

    @Binds
    fun provideEquipmentEntityMapper(mapper: EquipmentEntityMapper): Mapper<EquipmentEntity, Equipment>

    @Binds
    fun provideEquipmentResponseMapper(mapper: EquipmentResponseMapper): Mapper<HashMap<*, *>, Equipment>

    @Binds
    fun provideDeskResponseMapper(mapper: DeskResponseMapper): Mapper<HashMap<*, *>, DeskEntity>

    @Binds
    fun provideUserEntityMapper(mapper: UserEntityMapper): Mapper<UserEntity, User>

    @Binds
    fun providePreferences(preferencesStringStore: PreferencesStringStore): KeyValueStore<String>

    @Binds
    fun provideStringSetPreferences(preferences: PreferencesStringSetStore): KeyValueStore<Set<String>>
}