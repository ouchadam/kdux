package com.github.ouchadam.kdux.common

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

typealias KduxDisposable = () -> Unit
typealias Dispatch = (KduxAction) -> KduxDisposable
typealias Middleware<State> = (Store<State>) -> (Dispatch) -> Dispatch
typealias Reducer<State, Action> = (State, Action) -> State

interface Store<State> {
    fun getState(): State
    fun dispatch(action: KduxAction): KduxDisposable
    fun subscribe(listener: () -> Unit)
}

interface KduxAction

@ExperimentalContracts
inline fun <reified Impl> KduxAction.ensureType() {
    contract { returns() implies (this@ensureType is Impl) }
    if (this !is Impl) {
        throw IllegalStateException("unexpected action: $this")
    }
}
