package com.example.onmbarcode.presentation.login

interface LoginView {
    fun displayDeskScreen()
    fun enableLogin()
    fun disableLogin()
    fun displayWrongCredentialsMessage()
    fun hideErrorMessage()
    fun displayLoginFailedMessage()
    fun displaySettingsScreen()
}
