package com.meteoalgerie.autoscan.presentation.util.scheduler

import io.reactivex.Scheduler

interface SchedulerProvider {
    val worker: Scheduler
    val main: Scheduler
}