package com.github.ouchadam.kdux.common

class CompositeKduxDisposable: KduxDisposable {

    private val disposables = mutableListOf<KduxDisposable>()

    fun add(disposable: KduxDisposable) {
        this.disposables.add(disposable)
    }

    fun clear() {
        this.disposables.forEach { it.invoke() }
    }

    override fun invoke() {
        clear()
    }
}

operator fun CompositeKduxDisposable.plusAssign(disposable: KduxDisposable) {
    add(disposable)
}

fun KduxDisposable.addTo(compositeDisposable: CompositeKduxDisposable): KduxDisposable = this.apply { compositeDisposable.add(this) }
