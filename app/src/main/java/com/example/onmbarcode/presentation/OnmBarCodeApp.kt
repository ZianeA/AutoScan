package com.example.onmbarcode.presentation

import android.app.Activity
import android.app.Application
import androidx.work.Configuration
import com.example.onmbarcode.data.equipment.EquipmentDao
import com.example.onmbarcode.data.equipment.EquipmentEntityMapper
import com.example.onmbarcode.data.equipment.EquipmentRepository
import com.example.onmbarcode.data.equipment.EquipmentService
import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.presentation.di.DaggerAppComponent
import com.example.onmbarcode.presentation.equipment.Equipment
import com.example.onmbarcode.service.MyWorkerFactory
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class OnmBarCodeApp : Application(), HasActivityInjector, Configuration.Provider {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var workerFactory: MyWorkerFactory

    override fun onCreate() {
        DaggerAppComponent.factory()
            .create(this)
            .inject(this)

        super.onCreate()

    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector
}