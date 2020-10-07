package com.meteoalgerie.autoscan.common.scheduler

import io.reactivex.Scheduler

interface SchedulerProvider {
    val worker: Scheduler
    val main: Scheduler
}