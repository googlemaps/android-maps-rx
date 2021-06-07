package com.google.maps.android.rx.internal

import android.os.Looper
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeObserver
import io.reactivex.rxjava3.disposables.Disposable

/**
 * A Single that enforces that subscriptions occur on the Android main thread.
 */
internal abstract class MainThreadMaybe<T> : Maybe<T>() {
    override fun subscribeActual(observer: MaybeObserver<in T>) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            observer.onSubscribe(Disposable.empty())
            observer.onError(NotOnMainThreadException())
            return
        }
        subscribeMainThread(observer)
    }

    /**
     * Called on subscription once thread checks have been performed.
     */
    abstract fun subscribeMainThread(observer: MaybeObserver<in T>)
}
