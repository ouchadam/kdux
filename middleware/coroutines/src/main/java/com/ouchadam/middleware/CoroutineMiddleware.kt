package com.ouchadam.middleware

import com.ouchadam.kdux.common.KduxDisposable
import com.ouchadam.kdux.common.Middleware
import com.ouchadam.kdux.common.ReadState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

typealias KduxCoroutineSource<T> = suspend () -> Flow<T>

typealias CoroutineSourceFactory<State, Input, Output> = (ReadState<State?>, Input) -> KduxCoroutineSource<Output>

fun <State, Input, Output> coroutineMiddleware(
    factory: CoroutineSourceFactory<State, Input, Output>,
    scope: CoroutineScope
): Middleware<State, Input, Output> {
    return { input, readState ->
        { dispatch ->
            val source = factory(readState, input)
            scope.launch(Dispatchers.Main) {
                source().collect {
                    dispatch(it)
                }
            }.toKdux()
        }
    }
}

private fun Job.toKdux(): KduxDisposable = { this.cancel() }
