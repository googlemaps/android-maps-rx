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
import com.google.android.gms.maps.model.PointOfInterest
import com.google.maps.android.rx.internal.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Creates an [Observable] that emits whenever a point of interest is clicked.
 *
 * The created [Observable] uses [GoogleMap.setOnPoiClickListener] to listen to poi click events.
 * Since only one listener at a time is allowed, only one Observable at a time can be used.
 */
public fun GoogleMap.poiClickEvents(): Observable<PointOfInterest> =
    GoogleMapPoiClickObservable(this)

private class GoogleMapPoiClickObservable(
    private val googleMap: GoogleMap
) : MainThreadObservable<PointOfInterest>() {
    override fun subscribeMainThread(observer: Observer<in PointOfInterest>) {
        val listener = PoiClickListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnPoiClickListener(listener)
    }

    private class PoiClickListener(
        private val googleMap: GoogleMap,
        private val observer: Observer<in PointOfInterest>
    ) : MainThreadDisposable(), GoogleMap.OnPoiClickListener {

        override fun onDispose() {
            googleMap.setOnPoiClickListener(null)
        }

        override fun onPoiClick(poi: PointOfInterest) {
            if (!isDisposed) {
                observer.onNext(poi)
            }
        }
    }
}
