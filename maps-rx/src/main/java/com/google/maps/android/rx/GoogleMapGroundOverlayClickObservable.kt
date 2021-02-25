package com.google.maps.android.rx

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.GroundOverlay
import com.google.maps.android.rx.internal.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Creates an [Observable] that emits whenever a [GroundOverlay] is clicked on this [GoogleMap]
 * instance.
 *
 * The created [Observable] uses [GoogleMap.setOnGroundOverlayClickListener] to listen to ground
 * overlay clicks. Since only one listener at a time is allowed, only one Observable at a time can
 * be used.
 */
public fun GoogleMap.groundOverlayClickEvents(): Observable<GroundOverlay> =
    GoogleMapGroundOverlayObservable(this)

private class GoogleMapGroundOverlayObservable(
    private val googleMap: GoogleMap
) : MainThreadObservable<GroundOverlay>() {
    override fun subscribeMainThread(observer: Observer<in GroundOverlay>) {
        val listener = GroundOverlayClickListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnGroundOverlayClickListener(listener)
    }

    private class GroundOverlayClickListener(
        private val googleMap: GoogleMap,
        private val observer: Observer<in GroundOverlay>
    ) : MainThreadDisposable(), GoogleMap.OnGroundOverlayClickListener {
        override fun onGroundOverlayClick(groundOverlay: GroundOverlay) {
            if (!isDisposed) {
                observer.onNext(groundOverlay)
            }
        }

        override fun onDispose() {
            googleMap.setOnGroundOverlayClickListener(null)
        }
    }
}