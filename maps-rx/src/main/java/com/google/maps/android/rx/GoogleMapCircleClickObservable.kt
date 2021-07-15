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
import com.google.android.gms.maps.model.Circle
import com.google.maps.android.rx.shared.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Creates an [Observable] that emits whenever a [Circle] is clicked on this [GoogleMap] instance.
 *
 * The created [Observable] uses [GoogleMap.setOnCircleClickListener] to listen to circle clicks.
 * Since only one listener at a time is allowed, only one Observable at a time can be used.
 */
public fun GoogleMap.circleClickEvents(): Observable<Circle> =
    GoogleMapCircleClickObservable(this)

private class GoogleMapCircleClickObservable(
    private val googleMap: GoogleMap
) : MainThreadObservable<Circle>() {
    override fun subscribeMainThread(observer: Observer<in Circle>) {
        val listener = CircleClickListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnCircleClickListener(listener)
    }

    private class CircleClickListener(
        private val googleMap: GoogleMap,
        private val observer: Observer<in Circle>
    ) : MainThreadDisposable(), GoogleMap.OnCircleClickListener {
        override fun onCircleClick(circle: Circle) {
            if (!isDisposed) {
                observer.onNext(circle)
            }
        }

        override fun onDispose() {
            googleMap.setOnCircleClickListener(null)
        }
    }
}