package com.google.maps.android.rx

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.IndoorBuilding
import com.google.maps.android.rx.internal.MainThreadObservable
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * Event for an indoor state change
 */
public sealed class IndoorStateChangeEvent

/**
 * Event when the map focused on an indoor building (centered in viewport of selected by the user)
 */
public object IndoorBuildingFocusedEvent : IndoorStateChangeEvent()

/**
 * Event when an active level for a building is selected
 */
public class IndoorLevelActivatedEvent(
    public val indoorBuilding: IndoorBuilding
) : IndoorStateChangeEvent()

/**
 * Creates an [Observable] that emits whenever there is an indoor state change from a building.
 *
 * The created [Observable] uses [GoogleMap.setOnIndoorStateChangeListener] to listen to indoor
 * state changes. Since only one listener at a time is allowed, only one Observable at a time can
 * be used.
 */
public fun GoogleMap.indoorStateChangeEvents(): Observable<IndoorStateChangeEvent> =
    GoogleMapIndoorStateChangeObservable(this)

private class GoogleMapIndoorStateChangeObservable(
    private val googleMap: GoogleMap
) : MainThreadObservable<IndoorStateChangeEvent>() {
    override fun subscribeMainThread(observer: Observer<in IndoorStateChangeEvent>) {
        val listener = IndoorStateChangeListener(googleMap, observer)
        observer.onSubscribe(listener)
        googleMap.setOnIndoorStateChangeListener(listener)
    }

    private class IndoorStateChangeListener(
        private val googleMap: GoogleMap,
        private val observer: Observer<in IndoorStateChangeEvent>
    ) : MainThreadDisposable(), GoogleMap.OnIndoorStateChangeListener {

        override fun onDispose() {
            googleMap.setOnIndoorStateChangeListener(null)
        }

        override fun onIndoorBuildingFocused() {
            if (!isDisposed) {
                observer.onNext(IndoorBuildingFocusedEvent)
            }
        }

        override fun onIndoorLevelActivated(indoorBuilding: IndoorBuilding) {
            if (!isDisposed) {
                observer.onNext(IndoorLevelActivatedEvent(indoorBuilding))
            }
        }
    }
}