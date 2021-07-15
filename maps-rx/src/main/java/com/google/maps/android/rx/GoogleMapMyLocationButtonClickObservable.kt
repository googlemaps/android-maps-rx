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

package com.google.maps.android.rx

import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.rx.shared.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Creates an [Observable] that emits whenever the my location button is clicked.
 *
 * The created [Observable] uses [GoogleMap.setOnMyLocationButtonClickListener] to listen to map
 * my location button clicks. Since only one listener at a time is allowed, only one Observable at a
 * time can be used.
 *
 * @param consumed Lambda invoked when the my location button is clicked. The return value is the
 * value passed to [GoogleMap.OnMyLocationButtonClickListener.onMyLocationButtonClick]. Default
 * implementation will always return false
 */
public fun GoogleMap.myLocationButtonClickEvents(
    consumed: () -> Boolean = { false }
): Observable<Unit> =
    GoogleMapMyLocationButtonClickObservable(this, consumed)

private class GoogleMapMyLocationButtonClickObservable(
    private val googleMap: GoogleMap,
    private val consumed: () -> Boolean
) : MainThreadObservable<Unit>() {
    override fun subscribeMainThread(observer: Observer<in Unit>) {
        val listener = MyLocationButtonClickListener(googleMap, consumed, observer)
        observer.onSubscribe(listener)
        googleMap.setOnMyLocationButtonClickListener(listener)
    }

    private class MyLocationButtonClickListener(
        private val googleMap: GoogleMap,
        private val consumed: () -> Boolean,
        private val observer: Observer<in Unit>
    ) : MainThreadDisposable(), GoogleMap.OnMyLocationButtonClickListener {

        override fun onDispose() {
            googleMap.setOnMyLocationButtonClickListener(null)
        }

        override fun onMyLocationButtonClick(): Boolean {
            if (!isDisposed) {
                try {
                    if (consumed()) {
                        observer.onNext(Unit)
                        return true
                    }
                } catch (e: Exception) {
                    observer.onError(e)
                    dispose()
                }
                observer.onNext(Unit)
            }
            return false
        }
    }
}
