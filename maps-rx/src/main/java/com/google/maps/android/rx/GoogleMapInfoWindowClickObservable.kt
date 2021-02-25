package com.google.maps.android.rx

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.rx.internal.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Creates an [Observable] that emits whenever a marker's info window is clicked.
 *
 * The created [Observable] uses [GoogleMap.setOnInfoWindowClickListener] to listen to info window
 * clicks. Since only one listener at a time is allowed, only one Observable at a time can be used.
 */
public fun GoogleMap.infoWindowClickEvents(): Observable<Marker> =
    GoogleMapInfoWindowClickObservable(this)

private class GoogleMapInfoWindowClickObservable(
    private val googleMap: GoogleMap
) : MainThreadObservable<Marker>() {
    override fun subscribeMainThread(observer: Observer<in Marker>) {
        val listener = InfoWindowClickListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnInfoWindowClickListener(listener)
    }

    private class InfoWindowClickListener(
        private val googleMap: GoogleMap,
        private val observer: Observer<in Marker>
    ) : MainThreadDisposable(), GoogleMap.OnInfoWindowClickListener {

        override fun onDispose() {
            googleMap.setOnInfoWindowClickListener(null)
        }

        override fun onInfoWindowClick(marker: Marker) {
            if (!isDisposed) {
                observer.onNext(marker)
            }
        }
    }
}