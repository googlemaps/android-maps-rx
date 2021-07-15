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

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.rx.shared.MainThreadSingle
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver

/**
 * A Single that emits when a camera animation completes
 *
 * @param cameraUpdate the [CameraUpdate] to apply on the map
 * @param durationMs (optional) the duration in milliseconds of the animation
 * @return a Single emitting true if the animation completes, and false if the animation is canceled
 */
public fun GoogleMap.animateCamera(
    cameraUpdate: CameraUpdate,
    durationMs: Int? = null
): Single<Boolean> =
    GoogleMapAnimateCameraSingle(this, cameraUpdate, durationMs)

private class GoogleMapAnimateCameraSingle(
    private val googleMap: GoogleMap,
    private val cameraUpdate: CameraUpdate,
    private val durationMs: Int?
) : MainThreadSingle<Boolean>() {
    override fun subscribeMainThread(observer: SingleObserver<in Boolean>) {
        val listener = AnimationListener(googleMap, observer)
        observer.onSubscribe(listener)
        if (durationMs != null) {
            googleMap.animateCamera(cameraUpdate, durationMs, listener)
        } else {
            googleMap.animateCamera(cameraUpdate, listener)
        }
    }

    private class AnimationListener(
        private val googleMap: GoogleMap,
        private val observer: SingleObserver<in Boolean>
    ) : MainThreadDisposable(), GoogleMap.CancelableCallback {
        override fun onDispose() {
            googleMap.stopAnimation()
        }

        override fun onFinish() {
            if (!isDisposed) {
                observer.onSuccess(true)
            }
        }

        override fun onCancel() {
            if (!isDisposed) {
                observer.onSuccess(false)
            }
        }

    }
}