package com.meteoalgerie.autoscan.common.util

import io.reactivex.Completable
import io.reactivex.Observable

sealed class Lce<T> {
    class Loading<T> : Lce<T>()
    data class Content<T>(val data: T) : Lce<T>()
    data class Error<T>(val error: Throwable) : Lce<T>()
}

fun <T : Any> Observable<T>.toLce(): Observable<Lce<T>> {
    return this.map { Lce.Content(it) as Lce<T> }.onErrorReturn { Lce.Error(it) }
}

fun <T : Any> Completable.toLce(): Observable<Lce<T>> {
    return this.toObservable<T>().toLce<T>()
}

fun <T : Any, R : Any> Observable<Lce<T>>.mapLceContent(transform: (T) -> R): Observable<Lce<R>> {
    return this.map {
        when (it) {
            is Lce.Content -> Lce.Content<R>(transform(it.data))
            is Lce.Error -> Lce.Error<R>(it.error)
            is Lce.Loading -> Lce.Loading<R>()
        }
    }
}
