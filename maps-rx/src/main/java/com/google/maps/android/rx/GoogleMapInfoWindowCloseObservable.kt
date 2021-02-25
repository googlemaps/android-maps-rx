package com.google.maps.android.rx

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.rx.internal.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Creates an [Observable] that emits whenever a marker's info window is closed.
 *
 * The created [Observable] uses [GoogleMap.setOnInfoWindowCloseListener] to listen to info window
 * close events. Since only one listener at a time is allowed, only one Observable at a time can be
 * used.
 */
public fun GoogleMap.infoWindowCloseEvents(): Observable<Marker> =
    GoogleMapInfoWindowCloseObservable(this)

private class GoogleMapInfoWindowCloseObservable(
    private val googleMap: GoogleMap
) : MainThreadObservable<Marker>() {
    override fun subscribeMainThread(observer: Observer<in Marker>) {
        val listener = InfoWindowCloseListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnInfoWindowCloseListener(listener)
    }

    private class InfoWindowCloseListener(
        private val googleMap: GoogleMap,
        private val observer: Observer<in Marker>
    ) : MainThreadDisposable(), GoogleMap.OnInfoWindowCloseListener {

        override fun onDispose() {
            googleMap.setOnInfoWindowCloseListener(null)
        }

        override fun onInfoWindowClose(marker: Marker) {
            if (!isDisposed) {
                observer.onNext(marker)
            }
        }
    }
}