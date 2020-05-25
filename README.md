# kdux

A kotlin implementation of [redux](https://redux.js.org/)

Differences:

- dispatching within a `middleware` does not reiterate through other middlewares
- `middleware`must return a clean up function, `KduxDisposable`
- sub-reducers are not currently implemented

## Usage

`State` & `Action` can be any types, in these examples they are `String` for simplicity.

#### Simple usage

```kotlin
val reducer = { action: String, currentState: String?  ->
  if (action == "ACTION_START") "hello world" else currentState
}

val store = Store.create(reducer)
store.observe { println(it) }
store.post("ACTION_START")
```


#### Middleware

```kotlin
val logger = { action: String, readState: ReadState<String?> ->
    { dispatch: Dispatch<String> ->
        println("current state -> ${readState()}"
        dispatch(action)
        println("next state -> ${readState()}"
        SYNC_MIDDLEWARE
    }
}

...
val store = Store.create(reducer, logger)
store.post("ACTION_START")

// current state -> null
// next state -> hello world
```

##### Middleware can be chained together via `combineMiddleware`

chained `middleware` 

```kotlin
val middleware = combineMiddleware(
    fooMiddleware,
    logger
)

val store = Store.create(reducer, middleware)
```

##### rxjava-middleware

```kotlin
val factory = { readState: ReadState<String?>, action: String ->
    when (action) {
        "ACTION_START" -> Observable.just("async-result").toKdux()
        else -> Completable.complete().toKdux()
    }
}

val middleware = rxMiddleware(
    factory, 
    ioScheduler = Schedulers.io(), 
    reducerScheduler = AndroidSchedulers.mainThread()
)

val store = Store.create(reducer, middleware)
```

##### coroutines-middleware

```kotlin
private suspend fun backgroundWork() = withContext(Dispatchers.IO) { "async-result" }

val factory = { readState: ReadState<String?>, action: String ->
    when (action) {
        "ACTION_START" -> suspend { backgroundWork() }
        else -> suspend { throw IllegalStateException() }
    }
}

val middleware = coroutineMiddleware(factory, scope = GlobalScope)

val store = Store.create(reducer, middleware)
```

#### Clean up

`store.post(action)` returns a `KduxDisposable` which can be invoked in order to clean up references/resources

```kotlin
val disposables = CompositeKduxDisposable()
disposables += store.post("ACTION")
dispables.clear()
```
