package com.google.maps.android.rx

import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.rx.internal.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Creates an [Observable] that emits whenever the camera on this [GoogleMap] instance goes idle.
 *
 * The created [Observable] uses [GoogleMap.setOnCameraIdleListener] to listen to camera idle
 * events. Since only one listener at a time is allowed, only one Observable can be used.
 */
public fun GoogleMap.cameraIdleEvents(): Observable<Unit> =
    GoogleMapCameraIdleObservable(this)

private class GoogleMapCameraIdleObservable(
    private val googleMap: GoogleMap
) : MainThreadObservable<Unit>() {
    override fun subscribeMainThread(observer: Observer<in Unit>) {
        val listener = CameraIdleListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnCameraIdleListener(listener)
    }

    private class CameraIdleListener(
        private val googleMap: GoogleMap,
        private val observer: Observer<in Unit>
    ) : MainThreadDisposable(), GoogleMap.OnCameraIdleListener {
        override fun onCameraIdle() {
            if (!isDisposed) {
                observer.onNext(Unit)
            }
        }

        override fun onDispose() {
            googleMap.setOnCameraIdleListener(null)
        }
    }
}

