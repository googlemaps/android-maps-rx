package com.google.maps.android.rx

import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.rx.internal.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Creates an [Observable] that emits whenever a camera move is canceled on this [GoogleMap]
 * instance.
 *
 * The created [Observable] uses [GoogleMap.setOnCameraMoveCanceledListener] to listen to camera
 * move cancel events. Since only one listener at a time is allowed, only one Observable at a time
 * can be used.
 */
public fun GoogleMap.cameraMoveCanceledEvents(): Observable<Unit> =
    GoogleMapCameraMoveCanceledObservable(this)

private class GoogleMapCameraMoveCanceledObservable(
    private val googleMap: GoogleMap
) : MainThreadObservable<Unit>() {
    override fun subscribeMainThread(observer: Observer<in Unit>) {
        val listener = CameraMoveCanceledListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnCameraMoveCanceledListener(listener)
    }

    private class CameraMoveCanceledListener(
        private val googleMap: GoogleMap,
        private val observer: Observer<in Unit>
    ) : MainThreadDisposable(), GoogleMap.OnCameraMoveCanceledListener {
        override fun onCameraMoveCanceled() {
            if (!isDisposed) {
                observer.onNext(Unit)
            }
        }

        override fun onDispose() {
            googleMap.setOnCameraMoveCanceledListener(null)
        }
    }
}
