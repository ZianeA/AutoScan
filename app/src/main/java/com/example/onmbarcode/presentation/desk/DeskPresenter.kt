package com.example.onmbarcode.presentation.desk

import com.example.onmbarcode.presentation.di.FragmentScope
import com.example.onmbarcode.presentation.util.applySchedulers
import com.example.onmbarcode.presentation.util.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@FragmentScope
class DeskPresenter @Inject constructor(
    private val view: DeskView,
    private val deskRepository: DeskRepository,
    private val schedulerProvider: SchedulerProvider
) {
    val disposables = CompositeDisposable()

    fun start() {
        val disposable = deskRepository.getDesks()
            .applySchedulers(schedulerProvider)
            .subscribe({ view.displayDesks(it) }, { /*onError*/ })

        disposables.add(disposable)
    }

    fun stop() {
        disposables.clear()
    }
}