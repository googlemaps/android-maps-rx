package com.google.maps.android.rx

import android.graphics.Bitmap
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.rx.internal.MainThreadMaybe
import com.google.maps.android.rx.internal.MainThreadSingle
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeObserver
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver

/**
 * Creates a [Maybe] that emits a snapshot of the map when subscribed to.
 *
 * @param bitmap an optional bitmap to save the snapshot to. If not provided, a new bitmap will be
 * allocated
 * @return the Maybe emitting a snapshot of the map
 */
public fun GoogleMap.snapshot(bitmap: Bitmap? = null): Maybe<Bitmap> =
    GoogleMapSnapshotMaybe(this, bitmap)

private class GoogleMapSnapshotMaybe(
    private val googleMap: GoogleMap,
    private val bitmap: Bitmap?
) : MainThreadMaybe<Bitmap>() {
    override fun subscribeMainThread(observer: MaybeObserver<in Bitmap>) {
        val listener = SnapshotListener(observer)
        observer.onSubscribe(listener)
        if (bitmap == null) {
            googleMap.snapshot(listener)
        } else {
            googleMap.snapshot(listener, bitmap)
        }
    }

    private class SnapshotListener(
        private val observer: MaybeObserver<in Bitmap>
    ) : MainThreadDisposable(), GoogleMap.SnapshotReadyCallback {
        override fun onDispose() {
            // Do nothing
        }

        override fun onSnapshotReady(bitmap: Bitmap?) {
            if (!isDisposed) {
                if (bitmap == null) {
                    observer.onComplete()
                } else {
                    observer.onSuccess(bitmap)
                }
            }
        }
    }
}
