package com.github.ouchadam.kdux.setup

import android.util.Log
import com.github.ouchadam.kdux.common.Middleware

fun <State> loggerMiddleware(): Middleware<State> = { store ->
    { dispatch ->
        { action ->
            Log.e("!!!", "action: $action")
            val next = dispatch(action)
            Log.e("!!!", "state: ${store.getState()}")
            next
        }
    }
}
