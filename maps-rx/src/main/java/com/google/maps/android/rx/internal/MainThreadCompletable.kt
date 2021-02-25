package com.google.maps.android.rx.internal

import android.os.Looper
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.lang.IllegalStateException

/**
 * A Completable that enforces that subscriptions occur on the Android main thread.
 */
internal abstract class MainThreadCompletable : Completable() {
    override fun subscribeActual(observer: CompletableObserver) {
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
    abstract fun subscribeMainThread(observer: CompletableObserver)
}
