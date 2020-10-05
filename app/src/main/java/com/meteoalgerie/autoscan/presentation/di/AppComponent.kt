package com.meteoalgerie.autoscan.presentation.di

import android.app.Application
import com.meteoalgerie.autoscan.data.DatabaseModule
import com.meteoalgerie.autoscan.data.NetworkModule
import com.meteoalgerie.autoscan.data.RepositoryModule
import com.meteoalgerie.autoscan.presentation.OnmBarCodeApp
import com.meteoalgerie.autoscan.service.ServiceModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class, AppModule::class,
        ActivityBindingModule::class, DatabaseModule::class, RepositoryModule::class,
        ServiceModule::class, NetworkModule::class]
)
interface AppComponent {
    fun inject(app: OnmBarCodeApp)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: Application): AppComponent
    }
}