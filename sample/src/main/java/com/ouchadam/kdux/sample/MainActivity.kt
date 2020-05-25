package com.ouchadam.kdux.sample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.memrise.android.sample.R
import com.ouchadam.kdux.*
import com.ouchadam.middleware.CoroutineSourceFactory
import com.ouchadam.middleware.coroutineMiddleware
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bar()
    }

    fun bar() {
        val reducer = { action: String, currentState: String? ->
            if (action == "async-result") "hello world" else currentState
        }

        val factory = { readState: ReadState<String?>, action: String ->
            when (action) {
                "ACTION_START" -> suspend { backgroundWork() }
                else -> suspend { throw IllegalStateException() }
            }
        }

        val middleware = combineMiddleware(
            coroutineMiddleware(factory, GlobalScope),
            loggerMiddleware { Log.e("!!!", it) }
        )

        val store = Store.create(reducer, middleware)
        store.observe { }
        store.post("ACTION_START")
    }

    private suspend fun backgroundWork() = withContext(Dispatchers.IO) { "async-result" }
}
