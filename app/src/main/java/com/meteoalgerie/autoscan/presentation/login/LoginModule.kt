package com.meteoalgerie.autoscan.presentation.login

import dagger.Binds
import dagger.Module

@Module
interface LoginModule {
    @Binds
    fun provideLoginView(loginFragment: LoginFragment) : LoginView
}