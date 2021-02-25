package com.google.maps.android.rx

import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.rx.internal.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Creates an [Observable] that emits whenever the my location button is clicked.
 *
 * The created [Observable] uses [GoogleMap.setOnMyLocationButtonClickListener] to listen to map
 * my location button clicks. Since only one listener at a time is allowed, only one Observable at a
 * time can be used.
 *
 * @param consumed Lambda invoked when the my location button is clicked. The return value is the
 * value passed to [GoogleMap.OnMyLocationButtonClickListener.onMyLocationButtonClick]. Default
 * implementation will always return false
 */
public fun GoogleMap.myLocationButtonClickEvents(
    consumed: () -> Boolean = { false }
): Observable<Unit> =
    GoogleMapMyLocationButtonClickObservable(this, consumed)

private class GoogleMapMyLocationButtonClickObservable(
    private val googleMap: GoogleMap,
    private val consumed: () -> Boolean
) : MainThreadObservable<Unit>() {
    override fun subscribeMainThread(observer: Observer<in Unit>) {
        val listener = MyLocationButtonClickListener(googleMap, consumed, observer)
        observer.onSubscribe(listener)
        googleMap.setOnMyLocationButtonClickListener(listener)
    }

    private class MyLocationButtonClickListener(
        private val googleMap: GoogleMap,
        private val consumed: () -> Boolean,
        private val observer: Observer<in Unit>
    ) : MainThreadDisposable(), GoogleMap.OnMyLocationButtonClickListener {

        override fun onDispose() {
            googleMap.setOnMyLocationButtonClickListener(null)
        }

        override fun onMyLocationButtonClick(): Boolean {
            if (!isDisposed) {
                try {
                    if (consumed()) {
                        observer.onNext(Unit)
                        return true
                    }
                } catch (e: Exception) {
                    observer.onError(e)
                    dispose()
                }
                observer.onNext(Unit)
            }
            return false
        }
    }
}
