package com.google.maps.android.rx.internal

import android.os.Looper
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

/**
 * A Single that enforces that subscriptions occur on the Android main thread.
 */
internal abstract class MainThreadSingle<T> : Single<T>() {
    override fun subscribeActual(observer: SingleObserver<in T>) {
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
    abstract fun subscribeMainThread(observer: SingleObserver<in T>)
}
