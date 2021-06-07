// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
