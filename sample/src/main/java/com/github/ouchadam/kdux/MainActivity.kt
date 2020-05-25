package com.github.ouchadam.kdux

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.github.ouchadam.kdux.middleware.coroutineMiddleware
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MainActivity : Activity() {

    private lateinit var store: Store<State, UiAction, Action>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val reducer = { action: Action, currentState: State? ->
            when (action) {
                is Action.AsyncContent -> if (action.payload == "loading") {
                    State.Loading
                } else State.Content(action.payload)
            }
        }

        val factory = { readState: ReadState<State?>, action: UiAction ->
            when (action) {
                UiAction.Start -> suspend { backgroundWork() }
            }
        }

        val middleware = combineMiddleware(
            coroutineMiddleware(factory, GlobalScope),
            loggerMiddleware { Log.v("kdux", it) }
        )

        store = Store.create(reducer, middleware)
    }

    private suspend fun backgroundWork() = withContext(Dispatchers.IO) {
        flowOf("loading", "async-result").map {
            if (it == "async-result") {
                delay(2000)
            }
            Action.AsyncContent(it)
        }
    }

    override fun onStart() {
        super.onStart()
        store.observe {
            when (it) {
                State.Loading -> showToast("loading")
                is State.Content -> showToast(it.content)
            }
        }
        store.post(UiAction.Start)
    }

    private fun Activity.showToast(content: String) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
    }
}

sealed class State {
    object Loading : State()
    data class Content(val content: String) : State()
}

sealed class UiAction {
    object Start : UiAction()
}

sealed class Action {
    data class AsyncContent(val payload: String) : Action()
}