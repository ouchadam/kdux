package com.github.ouchadam.kdux

import com.github.ouchadam.kdux.common.Reducer

data class CombinedState4<S1, S2, S3, S4>(val s1: S1, val s2: S2, val s3: S3, val s4: S4)
data class CombinedState5<S1, S2, S3, S4, S5>(val s1: S1, val s2: S2, val s3: S3, val s4: S4, val s5: S5)

inline fun <S1 : Any, reified A1, S2 : Any, reified A2> combineReducers(
    crossinline r1: Reducer<S1, A1>,
    crossinline r2: Reducer<S2, A2>,
): Reducer<Pair<S1, S2>, Any> = { currentState: Pair<S1, S2>, action: Any ->
    when (action) {
        is A1 -> currentState.copy(first = r1.invoke(currentState.first, action as A1))
        is A2 -> currentState.copy(second = r2.invoke(currentState.second, action as A2))
        else -> throw IllegalStateException(action.toString())
    }
}

inline fun <S1 : Any, reified A1, S2 : Any, reified A2, S3 : Any, reified A3> combineReducers(
    crossinline r1: Reducer<S1, A1>,
    crossinline r2: Reducer<S2, A2>,
    crossinline r3: Reducer<S3, A3>,
): Reducer<Triple<S1, S2, S3>, Any> = { currentState: Triple<S1, S2, S3>, action: Any ->
    when (action) {
        is A1 -> currentState.copy(first = r1.invoke(currentState.first, action as A1))
        is A2 -> currentState.copy(second = r2.invoke(currentState.second, action as A2))
        is A3 -> currentState.copy(third = r3.invoke(currentState.third, action as A3))
        else -> throw IllegalStateException(action.toString())
    }
}

inline fun <S1 : Any, reified A1, S2 : Any, reified A2, S3 : Any, reified A3, S4 : Any, reified A4> combineReducers(
    crossinline r1: Reducer<S1, A1>,
    crossinline r2: Reducer<S2, A2>,
    crossinline r3: Reducer<S3, A3>,
    crossinline r4: Reducer<S4, A4>,
): Reducer<CombinedState4<S1, S2, S3, S4>, Any> = { currentState: CombinedState4<S1, S2, S3, S4>, action: Any ->
    when (action) {
        is A1 -> currentState.copy(s1 = r1.invoke(currentState.s1, action as A1))
        is A2 -> currentState.copy(s2 = r2.invoke(currentState.s2, action as A2))
        is A3 -> currentState.copy(s3 = r3.invoke(currentState.s3, action as A3))
        is A4 -> currentState.copy(s4 = r4.invoke(currentState.s4, action as A4))
        else -> throw IllegalStateException(action.toString())
    }
}

inline fun <S1 : Any, reified A1, S2 : Any, reified A2, S3 : Any, reified A3, S4 : Any, reified A4, S5 : Any, reified A5> combineReducers(
    crossinline r1: Reducer<S1, A1>,
    crossinline r2: Reducer<S2, A2>,
    crossinline r3: Reducer<S3, A3>,
    crossinline r4: Reducer<S4, A4>,
    crossinline r5: Reducer<S5, A5>,
): Reducer<CombinedState5<S1, S2, S3, S4, S5>, Any> = { currentState: CombinedState5<S1, S2, S3, S4, S5>, action: Any ->
    when (action) {
        is A1 -> currentState.copy(s1 = r1.invoke(currentState.s1, action as A1))
        is A2 -> currentState.copy(s2 = r2.invoke(currentState.s2, action as A2))
        is A3 -> currentState.copy(s3 = r3.invoke(currentState.s3, action as A3))
        is A4 -> currentState.copy(s4 = r4.invoke(currentState.s4, action as A4))
        is A5 -> currentState.copy(s5 = r5.invoke(currentState.s5, action as A5))
        else -> throw IllegalStateException(action.toString())
    }
}
