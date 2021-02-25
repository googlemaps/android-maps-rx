package com.google.maps.android.rx

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.rx.internal.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Creates an [Observable] that emits whenever the map is tapped.
 *
 * The created [Observable] uses [GoogleMap.setOnMapClickListener] to listen to map click events.
 * Since only one listener at a time is allowed, only one Observable at a time can be used.
 */
public fun GoogleMap.mapClickEvents(): Observable<LatLng> =
    GoogleMapOnMapClickObservable(this)

private class GoogleMapOnMapClickObservable(
    private val googleMap: GoogleMap
) : MainThreadObservable<LatLng>() {
    override fun subscribeMainThread(observer: Observer<in LatLng>) {
        val listener = MapClickListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnMapClickListener(listener)
    }

    private class MapClickListener(
        private val googleMap: GoogleMap,
        private val observer: Observer<in LatLng>
    ) : MainThreadDisposable(), GoogleMap.OnMapClickListener {

        override fun onDispose() {
            googleMap.setOnMapClickListener(null)
        }

        override fun onMapClick(latLng: LatLng) {
            if (!isDisposed) {
                observer.onNext(latLng)
            }
        }
    }
}
