package com.ouchadam.middleware

import kotlinx.coroutines.*

internal typealias ReadState<State> = () -> State
internal typealias Dispatch<Action> = (Action) -> Unit
internal typealias KduxDisposable = () -> Unit
internal typealias Middleware<State, Input, Output> = (Input, ReadState<State?>) -> (Dispatch<Output>) -> KduxDisposable

typealias KduxCoroutineSource<T> = suspend () -> T

typealias CoroutineSourceFactory<State, Input, Output> = (ReadState<State?>, Input) -> KduxCoroutineSource<Output>

fun <State, Input, Output> coroutineMiddleware(
    factory: CoroutineSourceFactory<State, Input, Output>,
    scope: CoroutineScope
): Middleware<State, Input, Output> {
    return { input, readState ->
        { dispatch ->
            val source = factory(readState, input)
            scope.launch(Dispatchers.Main) { dispatch(source()) }.toKdux()
        }
    }
}

fun Job.toKdux(): KduxDisposable = { this.cancel() }
