package com.example.onmbarcode.presentation.util.scheduler

import io.reactivex.Scheduler

interface SchedulerProvider {
    val worker: Scheduler
    val main: Scheduler
}