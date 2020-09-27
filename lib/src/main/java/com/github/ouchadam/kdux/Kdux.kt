package com.github.ouchadam.kdux

import com.github.ouchadam.kdux.common.*

typealias Enhancer<State, Action> = ((Reducer<State, Action>, State) -> Store<State>) -> (Reducer<State, Action>, State) -> Store<State>

fun <State, A : KduxAction> createStore(
    reducer: Reducer<State, A>,
    initialState: State,
    enhancer: Enhancer<State, A>,
): Store<State> = enhancer.invoke(::createStore).invoke(reducer, initialState)

fun <State, A : KduxAction> createStore(reducer: Reducer<State, A>, initialState: State) = object : Store<State> {
    private val listeners = mutableListOf<() -> Unit>()
    private var state = initialState

    override fun getState() = state

    override fun dispatch(action: KduxAction): KduxDisposable {
        state = reducer(state, action as A)
        listeners.forEach { it.invoke() }
        return {}
    }

    override fun subscribe(listener: () -> Unit): Unit = listeners.add(listener).ignoreValue()
}

fun <State, A : KduxAction> applyMiddleware(vararg middlewareFactories: Middleware<State>): Enhancer<State, A> = { createStore ->
    { reducer, state ->
        val store = createStore(reducer, state)
        val decoratedDispatch = store::dispatch.decorateDispatch(store, middlewareFactories.toList())
        store.replaceDispatch(decoratedDispatch)
    }
}

private fun <State> Dispatch.decorateDispatch(store: Store<State>, middlewareFactories: List<Middleware<State>>): Dispatch {
    var dispatch = { action: KduxAction -> this.invoke(action) }
    middlewareFactories.forEach { factory -> dispatch = factory(store)(dispatch) }
    return dispatch
}

private fun Any.ignoreValue() {}

private fun <State> Store<State>.replaceDispatch(dispatch: Dispatch) = object : Store<State> by this {
    override fun dispatch(action: KduxAction): KduxDisposable = dispatch.invoke(action)
}
