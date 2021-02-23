package com.google.maps.android.rx.internal

import android.os.Looper
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.lang.IllegalStateException

/**
 * An Observable that enforces that subscriptions occur on the Android main thread.
 */
internal abstract class MainThreadObservable<T> : Observable<T>() {
    override fun subscribeActual(observer: Observer<in T>) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            observer.onSubscribe(Disposable.empty())
            observer.onError(
                IllegalStateException(
                "Subscription is on ${Thread.currentThread().name} but must be on main thread."
            )
            )
            return
        }
        subscribeMainThread(observer)
    }

    /**
     * Called on subscription once thread checks have been performed.
     */
    abstract fun subscribeMainThread(observer: Observer<in T>)
}
