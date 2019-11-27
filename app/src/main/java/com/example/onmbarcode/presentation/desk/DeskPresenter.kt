package com.example.onmbarcode.presentation.desk

import com.example.onmbarcode.data.desk.DeskRepository
import com.example.onmbarcode.presentation.di.FragmentScope
import com.example.onmbarcode.presentation.util.Clock
import com.example.onmbarcode.presentation.util.applySchedulers
import com.example.onmbarcode.presentation.util.scheduler.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@FragmentScope
class DeskPresenter @Inject constructor(
    private val view: DeskView,
    private val deskRepository: DeskRepository,
    private val schedulerProvider: SchedulerProvider,
    private val clock: Clock
) {
    private val disposables = CompositeDisposable()

    fun start() {
         val disposable = deskRepository.getScannedDesks()
             .applySchedulers(schedulerProvider)
             .subscribe({ view.displayDesks(it) }, { /*onError*/ })

        disposables.add(disposable)
    }

    //TODO add yellow flashing animation similar to reddit and stackoverflow comment highlighting animation
    // maybe not because...
    // maybe yes when the user navigates back
    fun onBarcodeEntered(barcode: String) {
        val disposable = deskRepository.findDesk(barcode)
            .flatMap {
                deskRepository.updateDesk(
                    it.copy(
                        isScanned = true,
                        scanDate = clock.currentTimeMillis
                    )
                ).andThen(Single.just(it))
            }
            .applySchedulers(schedulerProvider)
            .subscribe({ view.displayEquipmentsScreen(it) }, { /*onError*/ })


        disposables.add(disposable)
    }

    fun stop() {
        disposables.clear()
    }
}