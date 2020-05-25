package com.ouchadam.kdux.middleware

import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

internal typealias ReadState<State> = () -> State
internal typealias Dispatch<Action> = (Action) -> Unit
internal typealias KduxDisposable = () -> Unit
internal typealias Middleware<State, Input, Output> = (Input, ReadState<State?>) -> (Dispatch<Output>) -> KduxDisposable

interface KduxSource<T> {
    fun subscribe(ioScheduler: Scheduler, uiScheduler: Scheduler, consumer: (T) -> Unit): KduxDisposable
}

typealias RxSourceFactory<State, Input, Output> = (ReadState<State?>, Input) -> KduxSource<Output>

fun <State, Input, Output> rxMiddleware(
    factory: RxSourceFactory<State, Input, Output>,
    ioScheduler: Scheduler,
    reducerScheduler: Scheduler
): Middleware<State, Input, Output> {
    return { input, readState ->
        { dispatch ->
            factory(readState, input).subscribe(ioScheduler, reducerScheduler) { dispatch(it) }
        }
    }
}

fun <T> Observable<T>.toKdux() = object: KduxSource<T> {
    override fun subscribe(ioScheduler: Scheduler, reducerScheduler: Scheduler, consumer: (T) -> Unit): KduxDisposable {
        return this@toKdux.subscribeOn(ioScheduler)
            .observeOn(reducerScheduler)
            .subscribe(consumer)
            .toKdux()
    }
}

fun <T> Maybe<T>.toKdux() = object: KduxSource<T> {
    override fun subscribe(ioScheduler: Scheduler, reducerScheduler: Scheduler, consumer: (T) -> Unit): KduxDisposable {
        return this@toKdux.subscribeOn(ioScheduler)
            .observeOn(reducerScheduler)
            .subscribe(consumer)
            .toKdux()
    }
}

fun <T> Single<T>.toKdux() = object: KduxSource<T> {
    override fun subscribe(ioScheduler: Scheduler, reducerScheduler: Scheduler, consumer: (T) -> Unit): KduxDisposable {
        return this@toKdux.subscribeOn(ioScheduler)
            .observeOn(reducerScheduler)
            .subscribe(consumer)
            .toKdux()
    }
}

fun <T> Completable.toKdux() = object: KduxSource<T> {
    override fun subscribe(ioScheduler: Scheduler, reducerScheduler: Scheduler, ignored: (T) -> Unit): KduxDisposable {
        return this@toKdux.subscribeOn(ioScheduler)
            .observeOn(reducerScheduler)
            .subscribe()
            .toKdux()
    }
}

private fun Disposable.toKdux(): KduxDisposable = {
    this.dispose()
}