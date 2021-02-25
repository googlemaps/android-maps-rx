package com.google.maps.android.rx

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.rx.internal.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Creates an [Observable] that emits whenever the map is long clicked.
 *
 * The created [Observable] uses [GoogleMap.setOnMapLongClickListener] to listen to map long click
 * events. Since only one listener at a time is allowed, only one Observable at a time can be used.
 */
public fun GoogleMap.mapLongClickEvents(): Observable<LatLng> =
    GoogleMapOnMapLongClickObservable(this)

private class GoogleMapOnMapLongClickObservable(
    private val googleMap: GoogleMap
) : MainThreadObservable<LatLng>() {
    override fun subscribeMainThread(observer: Observer<in LatLng>) {
        val listener = MapLongClickListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnMapLongClickListener(listener)
    }

    private class MapLongClickListener(
        private val googleMap: GoogleMap,
        private val observer: Observer<in LatLng>
    ) : MainThreadDisposable(), GoogleMap.OnMapLongClickListener {

        override fun onDispose() {
            googleMap.setOnMapLongClickListener(null)
        }

        override fun onMapLongClick(latLng: LatLng) {
            if (!isDisposed) {
                observer.onNext(latLng)
            }
        }
    }
}
