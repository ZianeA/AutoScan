package com.meteoalgerie.autoscan.util

import com.meteoalgerie.autoscan.common.scheduler.SchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers


class SyncSchedulerProvider : SchedulerProvider {
    override val worker: Scheduler = Schedulers.trampoline()
    override val main: Scheduler = Schedulers.trampoline()
}