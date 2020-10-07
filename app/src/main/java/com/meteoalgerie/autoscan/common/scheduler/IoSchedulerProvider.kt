package com.meteoalgerie.autoscan.common.scheduler

import dagger.Reusable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@Reusable
class IoSchedulerProvider @Inject constructor() : SchedulerProvider {
    override val worker: Scheduler = Schedulers.io()
    override val main: Scheduler = AndroidSchedulers.mainThread()
}