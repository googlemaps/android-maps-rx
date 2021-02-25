package com.google.maps.android.rx

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.maps.android.rx.internal.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Creates an [Observable] that emits whenever a [Circle] is clicked on this [GoogleMap] instance.
 *
 * The created [Observable] uses [GoogleMap.setOnCircleClickListener] to listen to circle clicks.
 * Since only one listener at a time is allowed, only one Observable at a time can be used.
 */
public fun GoogleMap.circleClickEvents(): Observable<Circle> =
    GoogleMapCircleClickObservable(this)

private class GoogleMapCircleClickObservable(
    private val googleMap: GoogleMap
) : MainThreadObservable<Circle>() {
    override fun subscribeMainThread(observer: Observer<in Circle>) {
        val listener = CircleClickListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnCircleClickListener(listener)
    }

    private class CircleClickListener(
        private val googleMap: GoogleMap,
        private val observer: Observer<in Circle>
    ) : MainThreadDisposable(), GoogleMap.OnCircleClickListener {
        override fun onCircleClick(circle: Circle) {
            if (!isDisposed) {
                observer.onNext(circle)
            }
        }

        override fun onDispose() {
            googleMap.setOnCircleClickListener(null)
        }
    }
}