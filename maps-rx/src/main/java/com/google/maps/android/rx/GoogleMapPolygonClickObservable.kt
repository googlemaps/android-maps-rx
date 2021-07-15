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
import com.google.android.gms.maps.model.Polygon
import com.google.maps.android.rx.shared.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Creates an [Observable] that emits whenever a polygon is clicked.
 *
 * The created [Observable] uses [GoogleMap.setOnPolygonClickListener] to listen to polygon click
 * events. Since only one listener at a time is allowed, only one Observable at a time can be used.
 */
public fun GoogleMap.polygonClickEvents(): Observable<Polygon> =
    GoogleMapPolygonClickObservable(this)

private class GoogleMapPolygonClickObservable(
    private val googleMap: GoogleMap
) : MainThreadObservable<Polygon>() {
    override fun subscribeMainThread(observer: Observer<in Polygon>) {
        val listener = PolygonClickListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnPolygonClickListener(listener)
    }

    private class PolygonClickListener(
        private val googleMap: GoogleMap,
        private val observer: Observer<in Polygon>
    ) : MainThreadDisposable(), GoogleMap.OnPolygonClickListener {

        override fun onDispose() {
            googleMap.setOnPolygonClickListener(null)
        }

        override fun onPolygonClick(polygon: Polygon) {
            if (!isDisposed) {
                observer.onNext(polygon)
            }
        }
    }
}
