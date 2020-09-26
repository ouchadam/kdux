package com.github.ouchadam.kdux.setup

import com.github.ouchadam.kdux.common.KduxAction

sealed class UiAction : KduxAction {
    object FetchContent : UiAction()
}

sealed class View1State {
    object Loading : View1State()
    data class Content(val content: String) : View1State()
}

sealed class View2State {
    object Loading : View2State()
    data class Content(val otherContent: String) : View2State()
}

sealed class AsyncAction : KduxAction {
    object AsyncLoading : AsyncAction()
    data class AsyncContent(val payload: String) : AsyncAction()
}

sealed class AsyncAction2 : KduxAction {
    object AsyncLoading : AsyncAction2()
    data class AsyncContent(val payload: String) : AsyncAction2()
}
