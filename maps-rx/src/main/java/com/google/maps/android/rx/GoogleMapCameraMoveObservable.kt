package com.google.maps.android.rx

import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.rx.internal.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Creates an [Observable] that emits whenever the camera on this [GoogleMap] instance moves.
 *
 * The created [Observable] uses [GoogleMap.setOnCameraMoveListener] to listen to camera move
 * events. Since only one listener at a time is allowed, only one Observable at a time can be used.
 */
public fun GoogleMap.cameraMoveEvents(): Observable<Unit> =
    GoogleMapCameraMoveObservable(this)

private class GoogleMapCameraMoveObservable(
    private val googleMap: GoogleMap
) : MainThreadObservable<Unit>() {
    override fun subscribeMainThread(observer: Observer<in Unit>) {
        val listener = CameraMoveListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnCameraMoveListener(listener)
    }

    private class CameraMoveListener(
        private val googleMap: GoogleMap,
        private val observer: Observer<in Unit>
    ) : MainThreadDisposable(), GoogleMap.OnCameraMoveListener {
        override fun onCameraMove() {
            if (!isDisposed) {
                observer.onNext(Unit)
            }
        }

        override fun onDispose() {
            googleMap.setOnCameraMoveListener(null)
        }
    }
}
