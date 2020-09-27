package com.github.ouchadam.kdux

import android.app.Activity
import android.os.Bundle
import com.github.ouchadam.kdux.common.*
import com.github.ouchadam.kdux.setup.*
import coroutineMiddleware
import kotlinx.coroutines.*

typealias ActivityState = Pair<View1State, View2State>

class MainActivity : Activity() {

    private val disposables = CompositeKduxDisposable()

    private lateinit var store: Store<ActivityState>
    private lateinit var view1: View1
    private lateinit var view2: View2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view1 = View1(findViewById(R.id.stateView1))
        view2 = View2(findViewById(R.id.stateView2))

        val reducer = { currentState: View1State, action: AsyncAction ->
            when (action) {
                AsyncAction.AsyncLoading -> View1State.Loading
                is AsyncAction.AsyncContent -> View1State.Content(action.payload)
            }
        }

        val reducer2 = { currentState: View2State, action: AsyncAction2 ->
            when (action) {
                AsyncAction2.AsyncLoading -> View2State.Loading
                is AsyncAction2.AsyncContent -> View2State.Content(action.payload)
            }
        }

        store = createStore(
            reducer = combineReducers(reducer, reducer2),
            initialState = Pair(View1State.Loading, View2State.Loading),
            enhancer = applyMiddleware(
                loggerMiddleware(),
                asyncMiddleware(),
            )
        )
    }

    override fun onStart() {
        super.onStart()

        store.subscribe {
            val (state1, state2) = store.getState()
            view1.bind(state1)
            view2.bind(state2)
        }

        disposables += store.dispatch(UiAction.FetchContent)
    }

    override fun onStop() {
        disposables.clear()
        super.onStop()
    }
}

private fun asyncMiddleware() = coroutineMiddleware<ActivityState, UiAction>(GlobalScope) { dispatch, action ->
    when (action) {
        UiAction.FetchContent -> {
            withContext(Dispatchers.IO) {
                dispatch(AsyncAction.AsyncLoading)
                dispatch(AsyncAction2.AsyncLoading)
                delay(2 * 1000)
                dispatch(AsyncAction.AsyncContent("result"))
                delay(1000)
                dispatch(AsyncAction2.AsyncContent("other result"))
            }
        }
    }
}
