package com.meteoalgerie.autoscan.desk

import androidx.room.EmptyResultSetException
import com.jakewharton.rxrelay2.BehaviorRelay
import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.common.database.PreferenceStorage
import com.meteoalgerie.autoscan.common.di.FragmentScope
import com.meteoalgerie.autoscan.common.util.Clock
import com.meteoalgerie.autoscan.common.util.applySchedulers
import com.meteoalgerie.autoscan.common.scheduler.SchedulerProvider
import hu.akarnokd.rxjava2.subjects.UnicastWorkSubject
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

@FragmentScope
class DeskPresenter @Inject constructor(
    private val deskRepository: DeskRepository,
    private val storage: PreferenceStorage,
    private val schedulerProvider: SchedulerProvider,
    private val clock: Clock
) {
    private val disposables = CompositeDisposable()

    val displayEmptyState = BehaviorRelay.create<Boolean>()
    val desks = BehaviorRelay.create<List<Desk>>()
    val canScan = BehaviorRelay.createDefault(true)
    val clearBarcodeBox = BehaviorRelay.create<Unit>()

    val message: UnicastWorkSubject<Int> = UnicastWorkSubject.create()
    val navigationDestination: UnicastWorkSubject<NavigationDestination> =
        UnicastWorkSubject.create()

    fun onStart() {
        val disposable = deskRepository.getScannedDesks()
            .applySchedulers(schedulerProvider)
            .subscribeBy(
                onNext = {
                    if (it.isEmpty()) {
                        displayEmptyState.accept(true)
                    } else {
                        displayEmptyState.accept(false)
                    }

                    desks.accept(it)
                },
                onError = { message.onNext(R.string.message_error_unknown) }
            )
        disposables.add(disposable)
    }

    fun onBarcodeEntered(barcode: String) {
        canScan.accept(false)

        val parsedBarcode = barcode.replace("\\s+".toRegex(), "").removePrefix("@")
        val disposable = deskRepository.findDesk(parsedBarcode)
            .flatMap {
                deskRepository.updateDesk(
                    it.copy(isScanned = true, isHidden = false, scanDate = clock.currentTimeSeconds)
                ).andThen(Single.just(it))
            }
            .applySchedulers(schedulerProvider)
            .subscribeBy(
                onSuccess = { desk ->
                    clearBarcodeBox.accept(Unit)
                    navigationDestination.onNext(NavigationDestination.Equipment(desk))
                    canScan.accept(true)
                },
                onError = { error ->
                    canScan.accept(true)
                    if (error is EmptyResultSetException) {
                        message.onNext(R.string.message_unknown_barcode)
                    } else {
                        message.onNext(R.string.message_error_unknown)
                    }
                }
            )

        disposables.add(disposable)
    }

    fun onLogout() {
        storage.user = null
        navigationDestination.onNext(NavigationDestination.Login)
    }

    fun onHideDeskClicked(desk: Desk) {
        val disposable = deskRepository.updateDesk(desk.copy(isHidden = true))
            .applySchedulers(schedulerProvider)
            .subscribeBy(
                onComplete = { message.onNext(R.string.message_desk_hidden) },
                onError = { message.onNext(R.string.message_error_unknown) }
            )

        disposables.add(disposable)
    }

    fun onDeskClicked(desk: Desk) {
        navigationDestination.onNext(NavigationDestination.Equipment(desk))
    }

    fun onCleared() {
        disposables.clear()
    }

    sealed class NavigationDestination {
        object Login : NavigationDestination()
        data class Equipment(val desk: Desk) : NavigationDestination()
    }
}