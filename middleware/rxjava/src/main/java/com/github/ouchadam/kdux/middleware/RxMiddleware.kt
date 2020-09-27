package com.github.ouchadam.kdux.middleware

import com.github.ouchadam.kdux.common.*
import io.reactivex.rxjava3.disposables.Disposable

typealias RxFactory<A> = ((KduxAction) -> Unit, A) -> Disposable

inline fun <State, reified A : KduxAction> rxMiddleware(crossinline factory: RxFactory<A>): Middleware<State> = { store ->
    { dispatch ->
        { action ->
            action.ensureType<A>()
            factory(dispatch, action).toKduxDisposable()
        }
    }
}

fun Disposable.toKduxDisposable(): KduxDisposable = {
    this.dispose()
}
