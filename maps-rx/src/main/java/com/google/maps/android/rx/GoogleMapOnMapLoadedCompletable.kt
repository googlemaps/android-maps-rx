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
import com.google.maps.android.rx.shared.MainThreadCompletable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver

/**
 * Creates a [Completable] that emits when the map is finished loading.
 *
 * The created [Completable] uses [GoogleMap.setOnMapLoadedCallback] to listen to camera move
 * events. Since only one listener at a time is allowed, only one Observable at a time can be used.
 */
public fun GoogleMap.mapLoad(): Completable =
    GoogleMapOnMapLoadedCompletable(this)

private class GoogleMapOnMapLoadedCompletable(
    private val googleMap: GoogleMap
) : MainThreadCompletable() {
    override fun subscribeMainThread(observer: CompletableObserver) {
        val listener = MapLoadListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnMapLoadedCallback(listener)
    }

    private class MapLoadListener(
        private val googleMap: GoogleMap,
        private val observer: CompletableObserver
    ) : MainThreadDisposable(), GoogleMap.OnMapLoadedCallback {

        override fun onDispose() {
            googleMap.setOnCameraMoveListener(null)
        }

        override fun onMapLoaded() {
            if (!isDisposed) {
                observer.onComplete()
            }
        }
    }
}
