package com.example.onmbarcode.util

import com.example.onmbarcode.presentation.util.scheduler.SchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers


class SyncSchedulerProvider : SchedulerProvider {
    override val worker: Scheduler = Schedulers.trampoline()
    override val main: Scheduler = Schedulers.trampoline()
}