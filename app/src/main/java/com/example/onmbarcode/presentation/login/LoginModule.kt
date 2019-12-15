package com.example.onmbarcode.presentation.login

import dagger.Binds
import dagger.Module

@Module
interface LoginModule {
    @Binds
    fun provideLoginView(loginFragment: LoginFragment) : LoginView
}