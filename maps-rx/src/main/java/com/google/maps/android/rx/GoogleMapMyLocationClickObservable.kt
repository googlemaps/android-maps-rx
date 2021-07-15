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

import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.rx.shared.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Creates an [Observable] that emits whenever the my location blue dot is clicked.
 *
 * The created [Observable] uses [GoogleMap.setOnMyLocationClickListener] to listen to my location
 * blue dot clicks. Since only one listener at a time is allowed, only one Observable at a time can
 * be used.
*/
public fun GoogleMap.myLocationClickEvents(): Observable<Location> =
    GoogleMapMyLocationClickObservable(this)

private class GoogleMapMyLocationClickObservable(
    private val googleMap: GoogleMap
) : MainThreadObservable<Location>() {
    override fun subscribeMainThread(observer: Observer<in Location>) {
        val listener = MyLocationClickListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnMyLocationClickListener(listener)
    }

    private class MyLocationClickListener(
        private val googleMap: GoogleMap,
        private val observer: Observer<in Location>
    ) : MainThreadDisposable(), GoogleMap.OnMyLocationClickListener {

        override fun onDispose() {
            googleMap.setOnMyLocationClickListener(null)
        }

        override fun onMyLocationClick(location: Location) {
            if (!isDisposed) {
                observer.onNext(location)
            }
        }
    }
}
