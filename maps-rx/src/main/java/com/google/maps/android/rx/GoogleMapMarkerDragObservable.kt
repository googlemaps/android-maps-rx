package com.google.maps.android.rx

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.rx.internal.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

public sealed class MarkerDragEvent {
    public abstract val marker: Marker
}

public data class DragStartEvent(override val marker: Marker): MarkerDragEvent()

public data class DragEvent(override val marker: Marker): MarkerDragEvent()

public data class DragEndEvent(override val marker: Marker): MarkerDragEvent()

/**
 * Creates an [Observable] that emits events when a marker is dragged.
 *
 * The created [Observable] uses [GoogleMap.setOnMarkerDragListener] to listen to map drag events.
 * Since only one listener at a time is allowed, only one Observable at a time can be used.
 */
public fun GoogleMap.markerDragEvents(): Observable<MarkerDragEvent> =
    GoogleMapMarkerDragObservable(this)

private class GoogleMapMarkerDragObservable(
    private val googleMap: GoogleMap
) : MainThreadObservable<MarkerDragEvent>() {
    override fun subscribeMainThread(observer: Observer<in MarkerDragEvent>) {
        val listener = MarkerDragListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnMarkerDragListener(listener)
    }

    private class MarkerDragListener(
        private val googleMap: GoogleMap,
        private val observer: Observer<in MarkerDragEvent>
    ) : MainThreadDisposable(), GoogleMap.OnMarkerDragListener {

        override fun onDispose() {
            googleMap.setOnMarkerDragListener(null)
        }

        override fun onMarkerDragStart(marker: Marker) {
            if (!isDisposed) {
                observer.onNext(DragStartEvent(marker))
            }
        }

        override fun onMarkerDrag(marker: Marker) {
            if (!isDisposed) {
                observer.onNext(DragEvent(marker))
            }
        }

        override fun onMarkerDragEnd(marker: Marker) {
            if (!isDisposed) {
                observer.onNext(DragEndEvent(marker))
            }
        }
    }
}
