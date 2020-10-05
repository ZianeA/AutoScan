package com.meteoalgerie.autoscan.presentation.desk

import com.meteoalgerie.autoscan.data.PreferenceStorage
import com.meteoalgerie.autoscan.data.desk.DeskRepository
import com.meteoalgerie.autoscan.presentation.di.FragmentScope
import com.meteoalgerie.autoscan.presentation.util.Clock
import com.meteoalgerie.autoscan.presentation.util.applySchedulers
import com.meteoalgerie.autoscan.presentation.util.scheduler.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@FragmentScope
class DeskPresenter @Inject constructor(
    private val view: DeskView,
    private val deskRepository: DeskRepository,
    private val storage: PreferenceStorage,
    private val schedulerProvider: SchedulerProvider,
    private val clock: Clock
) {
    private val disposables = CompositeDisposable()
    private var isBarcodeScanInProgress = false

    fun start() {
        val disposable = deskRepository.getScannedDesks()
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
        storage.user = null
        view.displayLoginScreen()
    }

    fun onHideDeskClicked(desk: Desk) {
        val disposable = deskRepository.updateDesk(desk.copy(isHidden = true))
            .applySchedulers(schedulerProvider)
            .subscribe({ }, { view.displayGenericErrorMessage() })

        disposables.add(disposable)
    }

    fun onDeskClicked(desk: Desk) {
        view.displayEquipmentsScreen(desk)
    }

    fun stop() {
        disposables.clear()
        isBarcodeScanInProgress = false
    }
}