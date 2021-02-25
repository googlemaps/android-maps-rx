package com.google.maps.android.rx

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.rx.internal.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Creates an [Observable] that emits whenever a marker's info window is long clicked.
 *
 * The created [Observable] uses [GoogleMap.setOnInfoWindowLongClickListener] to listen to info
 * window long clicks. Since only one listener at a time is allowed, only one Observable at a time
 * can be used.
 */
public fun GoogleMap.infoWindowLongClickEvents(): Observable<Marker> =
    GoogleMapInfoWindowLongClickObservable(this)

private class GoogleMapInfoWindowLongClickObservable(
    private val googleMap: GoogleMap
) : MainThreadObservable<Marker>() {
    override fun subscribeMainThread(observer: Observer<in Marker>) {
        val listener = InfoWindowLongClickListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnInfoWindowLongClickListener(listener)
    }

    private class InfoWindowLongClickListener(
        private val googleMap: GoogleMap,
        private val observer: Observer<in Marker>
    ) : MainThreadDisposable(), GoogleMap.OnInfoWindowLongClickListener {

        override fun onDispose() {
            googleMap.setOnInfoWindowLongClickListener(null)
        }

        override fun onInfoWindowLongClick(marker: Marker) {
            if (!isDisposed) {
                observer.onNext(marker)
            }
        }
    }
}