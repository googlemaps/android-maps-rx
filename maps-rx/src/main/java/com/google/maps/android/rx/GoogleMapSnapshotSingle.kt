package com.google.maps.android.rx

import android.graphics.Bitmap
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.rx.internal.MainThreadSingle
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver

/**
 * Creates a [Single] that emits a snapshot of the map when subscribed to.
 *
 * @param bitmap an optional bitmap to save the snapshot to. If not provided, a new bitmap will be
 * allocated
 * @return the Single emitting a snapshot of the map
 */
public fun GoogleMap.snapshotSingle(bitmap: Bitmap? = null): Single<Bitmap> =
    GoogleMapSnapshotSingle(this, bitmap)

private class GoogleMapSnapshotSingle(
    private val googleMap: GoogleMap,
    private val bitmap: Bitmap?
) : MainThreadSingle<Bitmap>() {
    override fun subscribeMainThread(observer: SingleObserver<in Bitmap>) {
        val listener = SnapshotListener(observer)
        observer.onSubscribe(listener)
        if (bitmap == null) {
            googleMap.snapshot(listener)
        } else {
            googleMap.snapshot(listener, bitmap)
        }
    }

    private class SnapshotListener(
        private val observer: SingleObserver<in Bitmap>
    ): MainThreadDisposable(), GoogleMap.SnapshotReadyCallback {
        override fun onDispose() {
            // Do nothing
        }

        override fun onSnapshotReady(bitmap: Bitmap) {
            if (!isDisposed) {
                observer.onSuccess(bitmap)
            }
        }
    }
}
