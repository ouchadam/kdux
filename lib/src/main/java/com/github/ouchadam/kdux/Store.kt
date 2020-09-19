package com.github.ouchadam.kdux

import com.github.ouchadam.kdux.common.KduxDisposable
import com.github.ouchadam.kdux.common.Middleware

typealias Observer<State> = (State) -> Unit
typealias Reducer<State, Action> = (Action, currentState: State?) -> State

typealias ReadState<State> = () -> State
typealias Dispatch<Action> = (Action) -> Unit

class Store<State, Action, TAction> private constructor(
    private val reducer: Reducer<State, TAction>,
    private val middleware: Middleware<State, Action, TAction>
) {

    companion object {
        fun <State, Action> create(reducer: Reducer<State, Action>) = Store(reducer, emptyMiddleware())

        fun <State, InputAction, Action> create(
            reducer: Reducer<State, Action>,
            middleware: Middleware<State, InputAction, Action>
        ) = Store(reducer, middleware)
    }

    private val observers = mutableListOf<Observer<State>>()
    private val stateHolder = StateHolder<State>(state = null)

    fun post(action: Action): KduxDisposable {
        val readState = { stateHolder.state }

        return middleware(action, readState).invoke { transformedAction ->
            val currentState = readState()
            val nextState = reducer(transformedAction, currentState)
            if (currentState != nextState) {
                updateState(nextState)
            }
        }
    }

    private fun updateState(nextState: State) {
        stateHolder.state = nextState
        notifyObservers(nextState)
    }

    private fun notifyObservers(newState: State) {
        this.observers.forEach { it.invoke(newState) }
    }

    fun observe(observer: Observer<State>) {
        this.observers.add(observer)
    }

    fun removeObserver(observer: Observer<State>) {
        this.observers.remove(observer)
    }

    fun clearObservers() {
        this.observers.clear()
    }
}

class StateHolder<State>(var state: State? = null)

