package com.google.maps.android.rx.places.internal

import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.SingleObserver

/**
 * A listener for completion events from a [Task] that emits results to a [SingleObserver].
 */
internal class TaskCompletionListener<T>(
    val cancellationTokenSource: CancellationTokenSource,
    private val observer: SingleObserver<in T>
) : MainThreadDisposable(), OnCompleteListener<T> {
    override fun onDispose() {
        cancellationTokenSource.cancel()
    }

    override fun onComplete(task: Task<T>) {
        val e = task.exception
        if (e == null) {
            if (task.isCanceled) {
                dispose()
            } else {
                observer.onSuccess(task.result)
            }
        } else {
            observer.onError(e)
        }
    }
}