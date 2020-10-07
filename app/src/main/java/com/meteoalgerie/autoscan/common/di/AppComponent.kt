package com.meteoalgerie.autoscan.common.di

import android.app.Application
import com.meteoalgerie.autoscan.common.database.DatabaseModule
import com.meteoalgerie.autoscan.common.network.NetworkModule
import com.meteoalgerie.autoscan.common.OnmBarCodeApp
import com.meteoalgerie.autoscan.equipment.service.ServiceModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class, AppModule::class,
        ActivityBindingModule::class, DatabaseModule::class, ServiceModule::class,
        NetworkModule::class]
)
interface AppComponent {
    fun inject(app: OnmBarCodeApp)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: Application): AppComponent
    }
}