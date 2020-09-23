package com.meteoalgerie.autoscan.presentation.desk

import com.meteoalgerie.autoscan.data.desk.DeskRepository
import com.meteoalgerie.autoscan.data.user.UserRepository
import com.meteoalgerie.autoscan.presentation.di.FragmentScope
import com.meteoalgerie.autoscan.presentation.util.Clock
import com.meteoalgerie.autoscan.presentation.util.applySchedulers
import com.meteoalgerie.autoscan.presentation.util.scheduler.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@FragmentScope
class DeskPresenter @Inject constructor(
    private val view: DeskView,
    private val deskRepository: DeskRepository,
    private val userRepository: UserRepository,
    private val schedulerProvider: SchedulerProvider,
    private val clock: Clock
) {
    private val disposables = CompositeDisposable()
    private var isBarcodeScanInProgress = false

    fun start() {
        view.disableBarcodeInput()
        if (deskRepository.isDownloadComplete().not()) view.displayDownloadViews()

        val disposable =
            deskRepository.downloadDatabase()
                .observeOn(schedulerProvider.main)
                .doOnNext {
                    view.setDownloadProgress(it)

                    if (it >= 100) {
                        view.hideDownloadViews()
                    }
                }
                .doOnError { view.indicateDownloadPending() }
                .observeOn(schedulerProvider.worker)
                .retryWhen { it.delay(1, TimeUnit.SECONDS) }
                .toList()
                .flatMapObservable { deskRepository.getScannedDesks() }
                .applySchedulers(schedulerProvider)
                .subscribe({
                    if (it.isEmpty()) view.displayScanDeskMessage()

                    view.displayDesks(it)
                    view.enableBarcodeInput()
                }, {
                    view.displayGenericErrorMessage()
                })
        disposables.add(disposable)
    }

    //TODO Add yellow flashing animation when the user navigates back.
    // Similar to reddit and stackoverflow comment highlighting animation.
    fun onBarcodeEntered(barcode: String) {
        if (isBarcodeScanInProgress) return

        isBarcodeScanInProgress = true
        view.disableBarcodeInput()
        val parsedBarcode = barcode.replace("\\s+".toRegex(), "").removePrefix("@")
        val disposable = deskRepository.findDesk(parsedBarcode)
            .observeOn(schedulerProvider.main)
            .doOnSuccess { view.clearBarcodeInputArea() }
            .observeOn(schedulerProvider.worker)
            .flatMap {
                deskRepository.updateDesk(
                    it.copy(
                        isScanned = true,
                        isHidden = false,
                        scanDate = clock.currentTimeSeconds
                    )
                ).andThen(Single.just(it))
                    .toMaybe()
            }
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

    fun onHideDeskClicked(desk: Desk) {
        val disposable = deskRepository.updateDesk(desk.copy(isHidden = true))
            .applySchedulers(schedulerProvider)
            .subscribe({ }, { view.displayGenericErrorMessage() })

        disposables.add(disposable)
    }

    fun stop() {
        disposables.clear()
        isBarcodeScanInProgress = false
    }

    fun onDeskClicked(desk: Desk) {
        view.displayEquipmentsScreen(desk)
    }
}