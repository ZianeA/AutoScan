package com.example.onmbarcode.presentation.di

import android.app.Application
import com.example.onmbarcode.presentation.OnmBarCodeApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, ActivityBindingModule::class])
interface AppComponent {
    fun inject(app: OnmBarCodeApp)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: Application): AppComponent
    }
}