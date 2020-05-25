package com.ouchadam.kdux.sample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.memrise.android.sample.R
import com.ouchadam.kdux.*
import com.ouchadam.kdux.middleware.RxSourceFactory
import com.ouchadam.kdux.middleware.rxMiddleware
import com.ouchadam.kdux.middleware.toKdux
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

typealias State = String
typealias Action = String

class MainActivity : Activity() {

    private val disposables = CompositeKduxDisposable()
    private lateinit var store: Store<State, Action, Action>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bar()
    }

//    override fun onStart() {
//        super.onStart()
//        store.observe {
//            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
//        }
//
//        disposables += store.post("started")
//    }
//
//    override fun onStop() {
//        super.onStop()
//        store.clearObservers()
//        disposables.clear()
//    }

    fun bar() {
        val reducer = { action: String, currentState: String? ->
            if (action == "async-result") "hello world" else currentState
        }

        val logger = { input: String, readState: ReadState<String?> ->
            { dispatch: Dispatch<String> ->
                Log.e("!!!", " current state ${readState()}")
                dispatch(input)
                Log.e("!!!", " next state ${readState()}")
                SYNC_MIDDLEWARE
            }
        }

        val factory = { readState: ReadState<String?>, action: String ->
            when (action) {
                "ACTION_START" -> Observable.just("async-result").toKdux()
                else -> Completable.complete().toKdux()
            }
        }

        val middleware = combineMiddleware(
            rxMiddleware(factory, ioScheduler = Schedulers.io(), reducerScheduler = AndroidSchedulers.mainThread()),
            logger
        )

        val store = Store.create(reducer, middleware)
        store.observe { }
        store.post("ACTION_START")
    }

    fun bar2() {
        val reducer = { action: String, currentState: String? ->
            if (action == "ACTION_START") "hello world" else currentState
        }

        val store = Store.create(reducer)

        store.observe { Log.e("!!!", it ?: "empty") }
        store.post("ACTION_START")
    }
}
