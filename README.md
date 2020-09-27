# kdux

[ ![Download](https://api.bintray.com/packages/ouchadam/maven/kdux/images/download.svg) ](https://bintray.com/ouchadam/maven/kdux/_latestVersion) [![CircleCI](https://circleci.com/gh/ouchadam/kdux.svg?style=svg)](https://circleci.com/gh/ouchadam/kdux)


A kotlin implementation of [redux](https://redux.js.org/)

```gradle
implementation "com.github.ouchadam:kdux:$version"

//optional
implementation "com.github.ouchadam:kdux-middleware-rxjava-3:$version"
implementation "com.github.ouchadam:kdux-middleware-coroutines:$version"
```

_tl;dr_

```kotlin
val store = createStore(
    reducer = combineReducers(reducer1, reducer2),
    initialState = Pair(View1State.Loading, View2State.Loading),
    enhancer = applyMiddleware(
        loggerMiddleware(),
        asyncMiddleware(),
    )
)
```

#### Simple usage

```kotlin
enum class MyAction: KduxAction { START }

val reducer = { currentState: String, action: MyAction  ->
    when(action) {
        MyAction.START -> "hello world"
    }
}

val store = createStore(reducer, initialState = "empty")
store.subscribe { println(store.getState()) }
store.dispatch(MyAction.START)
```

#### Combining reducers
```kotlin
enum class MyAction: KduxAction { START }
enum class OtherActions: KduxAction { INCREMENT }

val reducer1 = { currentState: String, action: MyAction  ->
    when(action) {
        MyAction.START -> "hello world"
    }
}

val reducer2 = { currentState: Int, action: OtherActions  ->
    when(action) {
        OtherActions.INCREMENT -> currentState + 1
    }
}

val reducer = combineReducers(reducer1, reducer2)
val store = createStore(reducer, initialState = Pair("empty", -1))
store.subscribe { println(store.getState()) }
store.dispatch(MyAction.START)
store.dispatch(OtherActions.INCREMENT)
```

#### Middleware

```kotlin
fun <State> loggerMiddleware(): Middleware<State> = { store ->
    { dispatch ->
        { action ->
            println("action: $action")
            val next = dispatch(action)
            println("state: ${store.getState()}")
            next
        }
    }
}

val store = createStore(
    reducer = combineReducers(reducer, reducer2),
    initialState = Pair(View1State.Loading, View2State.Loading),
    enhancer = applyMiddleware(loggerMiddleware())
)
```

#### Async middleware

##### build your own

```kotlin
fun threadMiddleware(): Middleware<State> = { store ->
    { dispatch ->
        { action ->
            action.ensureType<UiAction>()
            when (action) {
                UiAction.FetchContent -> {
                    thread { 
                        dispatch(AsyncAction.AsyncContent("result")) 
                    }.run { { interrupt() } }
                }
            }
        }
    }
}
```

##### or use a built-in helper

##### rxjava-middleware

```kotlin
private fun middleware() = rxMiddleware<State, MyAction> { dispatch, action ->
    when (action) {
        MyAction.START -> {
            val source = Single.just(AsyncAction.Result("payload"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

            source.subscribe { result -> dispatch(result) }
        }
    }
}
```

##### coroutines-middleware

```kotlin
private fun middleware() = coroutineMiddleware<ActivityState, UiAction>(GlobalScope) { dispatch, action ->
    when (action) {
        UiAction.FetchContent -> {
            withContext(Dispatchers.IO) {
                dispatch(AsyncAction.Result("payload"))
            }
        }
    }
}
```

#### Clean up

```kotlin
val disposables = CompositeKduxDisposable()
disposables += store.dispatch(MyAction.START)
dispables.clear()
```
