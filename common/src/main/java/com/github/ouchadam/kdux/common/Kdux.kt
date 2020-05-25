package com.github.ouchadam.kdux.common

typealias ReadState<State> = () -> State
typealias Dispatch<Action> = (Action) -> Unit
typealias KduxDisposable = () -> Unit
typealias Middleware<State, Input, Output> = (Input, ReadState<State?>) -> (Dispatch<Output>) -> KduxDisposable
