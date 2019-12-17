package com.example.onmbarcode.presentation.di

import android.app.Application
import com.example.onmbarcode.data.DatabaseModule
import com.example.onmbarcode.data.RepositoryModule
import com.example.onmbarcode.presentation.OnmBarCodeApp
import com.example.onmbarcode.service.ServiceModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class, AppModule::class,
        ActivityBindingModule::class, DatabaseModule::class, RepositoryModule::class,
        ServiceModule::class]
)
interface AppComponent {
    fun inject(app: OnmBarCodeApp)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: Application): AppComponent
    }
}