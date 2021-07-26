package com.google.maps.android.rx.places.internal

import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.rx.shared.MainThreadSingle
import io.reactivex.rxjava3.core.SingleObserver

/**
 * A subclass of [Single] to be used for wrapping a [Task]
 */
internal abstract class MainThreadTaskSingle<T> : MainThreadSingle<T>() {
    override fun subscribeMainThread(observer: SingleObserver<in T>) {
        val cancellationTokenSource = CancellationTokenSource()
        val listener = TaskCompletionListener(cancellationTokenSource, observer)
        invokeRequest(listener)
        observer.onSubscribe(listener)
    }

    abstract fun invokeRequest(listener: TaskCompletionListener<T>)
}