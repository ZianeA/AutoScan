package com.example.onmbarcode.presentation.desk

import android.database.sqlite.SQLiteDatabaseCorruptException
import com.example.onmbarcode.data.KeyValueStore
import com.example.onmbarcode.data.PreferencesStringStore
import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.data.desk.DeskRepository
import com.example.onmbarcode.data.equipment.EquipmentRepository
import com.example.onmbarcode.data.user.UserRepository
import com.example.onmbarcode.presentation.di.FragmentScope
import com.example.onmbarcode.presentation.util.Clock
import com.example.onmbarcode.presentation.util.applySchedulers
import com.example.onmbarcode.presentation.util.scheduler.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@FragmentScope
class DeskPresenter @Inject constructor(
    private val view: DeskView,
    private val deskRepository: DeskRepository,
    private val userRepository: UserRepository,
    private val equipmentRepository: EquipmentRepository,
    private val store: KeyValueStore<String>,
    private val schedulerProvider: SchedulerProvider,
    private val clock: Clock
) {
    private val disposables = CompositeDisposable()
    private var isBarcodeScanInProgress = false

    fun start() {
        view.disableBarcodeInput()

        val disposable =
            deskRepository.isDatabaseEmpty()
                .flatMapCompletable { isDatabaseEmpty ->
                    if (isDatabaseEmpty) {
                        deskRepository.downloadDatabase()
                    } else {
                        equipmentRepository.getAllEquipmentCount()
                            .flatMapCompletable {
                                val t = store.get(PreferencesStringStore.EQUIPMENT_COUNT_KEY, "0")
                                val isDatabaseCorrupted =
                                    store.get(PreferencesStringStore.EQUIPMENT_COUNT_KEY, "0")
                                        .equals(it.toString(), true).not()
                                if (isDatabaseCorrupted) {
                                    deskRepository.deleteAllDesks()
                                        .andThen(equipmentRepository.deleteAllEquipments())
                                        .andThen(Completable.error(SQLiteDatabaseCorruptException("Database was not fully downloaded")))
                                } else Completable.complete()
                            }
                    }
                }
                .retry()
                .andThen(deskRepository.getScannedDesks())
                .applySchedulers(schedulerProvider)
                .subscribe({
                    view.displayDesks(it)
                    view.enableBarcodeInput()
                }, {
                    val t = it
                    view.displayGenericErrorMessage()
                })

        disposables.add(disposable)
    }

    //TODO add yellow flashing animation similar to reddit and stackoverflow comment highlighting animation
    // maybe not because...
    // maybe yes when the user navigates back
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

    fun stop() {
        disposables.clear()
        isBarcodeScanInProgress = false
    }

    fun onDeskClicked(desk: Desk) {
        view.displayEquipmentsScreen(desk)
    }
}