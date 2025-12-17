/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.maps.android.rx.places.internal

import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.SingleObserver

/**
 * A listener for completion events from a [Task] that emits results to a [SingleObserver].
 */
internal class TaskCompletionListener<T : Any>(
    val cancellationTokenSource: CancellationTokenSource,
    private val observer: SingleObserver<in T>
) : MainThreadDisposable(), OnCompleteListener<T> {
    override fun onDispose() {
        cancellationTokenSource.cancel()
    }

    override fun onComplete(task: Task<T>) {
        if (task.isCanceled) {
            dispose()
            return
        }

        val e = task.exception
        if (e != null) {
            observer.onError(e)
            return
        }

        observer.onSuccess(task.result)
    }
}