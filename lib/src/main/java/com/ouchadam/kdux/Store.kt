package com.ouchadam.kdux

typealias Observer<State> = (State) -> Unit
typealias KduxDisposable = () -> Unit
typealias Reducer<State, Action> = (Action, currentState: State?) -> State

typealias ReadState<State> = () -> State
typealias Dispatch<Action> = (Action) -> Unit
typealias Middleware<State, Input, Output> = (Input, ReadState<State?>) -> (Dispatch<Output>) -> KduxDisposable

val SYNC_MIDDLEWARE: KduxDisposable = {}

private fun <State, Input> emptyMiddleware(): Middleware<State, Input, Input> {
    return { input, _ ->
        { dispatch ->
            dispatch(input)
            SYNC_MIDDLEWARE
        }
    }
}

class Store<State, Action, TAction> private constructor(
    private val reducer: Reducer<State, TAction>,
    private val middleware: Middleware<State, Action, TAction>
) {

    companion object {
        fun <State, Action> create(reducer: Reducer<State, Action>): Store<State, Action, Action> {
            return Store(reducer, emptyMiddleware())
        }

        fun <State, InputAction, Action> create(
            reducer: Reducer<State, Action>,
            middleware: Middleware<State, InputAction, Action>
        ): Store<State, InputAction, Action> {
            return Store(reducer, middleware)
        }
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


fun <S, M1I, M1O, M2O> combineMiddleware(
    m1: Middleware<S, M1I, M1O>,
    m2: Middleware<S, M1O, M2O>
): Middleware<S, M1I, M2O> = { input, readState ->
    { dispatch ->
        val disposables = CompositeKduxDisposable()
        disposables += m1(input, readState).invoke { result ->
            disposables += m2(result, readState).invoke { result ->
                dispatch(result)
            }
        }
        disposables
    }
}

class StateHolder<State>(var state: State? = null)
