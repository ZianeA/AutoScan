package com.example.onmbarcode.presentation.desk

import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.data.desk.DeskRepository
import com.example.onmbarcode.data.user.UserRepository
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
    private val userRepository: UserRepository,
    private val schedulerProvider: SchedulerProvider,
    private val clock: Clock,
    private val DeskUiMapper: Mapper<DeskUi, Desk>
) {
    private val disposables = CompositeDisposable()
    private var isBarcodeScanInProgress = false

    fun start() {
        val disposable = deskRepository.getScannedDesks()
            .map { it.map(DeskUiMapper::mapReverse) }
            .applySchedulers(schedulerProvider)
            .subscribe({ view.displayDesks(it) }, { /*onError*/ })

        disposables.add(disposable)
    }

    //TODO add yellow flashing animation similar to reddit and stackoverflow comment highlighting animation
    // maybe not because...
    // maybe yes when the user navigates back
    fun onBarcodeEntered(barcode: String) {
        if (isBarcodeScanInProgress) return

        isBarcodeScanInProgress = true
        view.disableBarcodeInput()
        val disposable = deskRepository.findDesk(barcode.replace("\\s+".toRegex(),""))
            .observeOn(schedulerProvider.main)
            .doOnSuccess { view.clearBarcodeInputArea() }
            .observeOn(schedulerProvider.worker)
            .flatMap {
                deskRepository.updateDesk(
                    it.copy(
                        isScanned = true,
                        scanDate = clock.currentTimeSeconds
                    )
                ).andThen(Single.just(it))
                    .toMaybe()
            }
            .map(DeskUiMapper::mapReverse)
            .applySchedulers(schedulerProvider)
            .subscribe(
                { view.displayEquipmentsScreen(it) },
                {
                    isBarcodeScanInProgress = false
                    view.enableBarcodeInput()
                    view.displayGenericErrorMessage()
                },
                {
                    isBarcodeScanInProgress = false
                    view.enableBarcodeInput()
                    view.displayUnknownBarcodeMessage()
                }
            )


        disposables.add(disposable)
    }

    fun onLogout() {
        val disposable = userRepository.removeUser()
            .applySchedulers(schedulerProvider)
            .subscribe({ view.displayLoginScreen() }, { view.displayGenericErrorMessage() })

        disposables.add(disposable)
    }

    fun stop() {
        disposables.clear()
        isBarcodeScanInProgress = false
    }
}