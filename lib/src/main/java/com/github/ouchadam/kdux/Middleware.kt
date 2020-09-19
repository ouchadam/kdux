package com.github.ouchadam.kdux

import com.github.ouchadam.kdux.common.CompositeKduxDisposable
import com.github.ouchadam.kdux.common.KduxDisposable
import com.github.ouchadam.kdux.common.Middleware
import com.github.ouchadam.kdux.common.plusAssign

private val SYNC_MIDDLEWARE: KduxDisposable = {}

fun <S, M1I, M1O, M2O> combineMiddleware(
    m1: Middleware<S, M1I, M1O>,
    m2: Middleware<S, M1O, M2O>
): Middleware<S, M1I, M2O> = { input, readState ->
    { dispatch ->
        val disposables =
            CompositeKduxDisposable()
        disposables += m1(input, readState).invoke { result ->
            disposables += m2(result, readState).invoke { result ->
                dispatch(result)
            }
        }
        disposables
    }
}

fun<State, Input> loggerMiddleware(log: (String) -> Unit) = { input: Input, readState: ReadState<State?> ->
    { dispatch: Dispatch<Input> ->
        log("current state ${readState()}")
        dispatch(input)
        log("next state ${readState()}")
        SYNC_MIDDLEWARE
    }
}

fun <State, Input> emptyMiddleware(): Middleware<State, Input, Input> {
    return { input, _ ->
        { dispatch ->
            dispatch(input)
            SYNC_MIDDLEWARE
        }
    }
}


fun <State, Input, Output> syncMiddleware(provider: (Input, ReadState<State?>) -> Output): Middleware<State, Input, Output> {
    return { input, readState ->
        { dispatch ->
            dispatch(provider(input, readState))
            SYNC_MIDDLEWARE
        }
    }
}
