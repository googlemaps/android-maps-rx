package com.google.maps.android.rx

import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.rx.internal.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Creates an [Observable] that emits whenever a camera move started on this [GoogleMap] instance.
 *
 * The created [Observable] uses [GoogleMap.setOnCameraMoveStartedListener] to listen to camera
 * move started events. Since only one listener at a time is allowed, only one Observable at a time
 * can be used.
 */
public fun GoogleMap.cameraMoveStartedEvents(): Observable<Int> =
    GoogleMapCameraMoveStartedObservable(this)

private class GoogleMapCameraMoveStartedObservable(
    private val googleMap: GoogleMap
) : MainThreadObservable<Int>() {
    override fun subscribeMainThread(observer: Observer<in Int>) {
        val listener = CameraMoveStartedListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnCameraMoveStartedListener(listener)
    }

    private class CameraMoveStartedListener(
        private val googleMap: GoogleMap,
        private val observer: Observer<in Int>
    ) : MainThreadDisposable(), GoogleMap.OnCameraMoveStartedListener {

        override fun onDispose() {
            googleMap.setOnCameraMoveStartedListener(null)
        }

        override fun onCameraMoveStarted(reason: Int) {
            if (!isDisposed) {
                observer.onNext(reason)
            }
        }
    }
}
