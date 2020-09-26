package com.github.ouchadam.kdux.setup

import android.widget.TextView

class View1(private val textView: TextView) {

    fun bind(state: View1State) {
        when (state) {
            View1State.Loading -> textView.text = "Loading"
            is View1State.Content -> textView.text = state.content
        }
    }

}

class View2(private val textView: TextView) {

    fun bind(state: View2State) {
        when (state) {
            View2State.Loading -> textView.text = "Loading"
            is View2State.Content -> textView.text = state.otherContent
        }
    }

}
