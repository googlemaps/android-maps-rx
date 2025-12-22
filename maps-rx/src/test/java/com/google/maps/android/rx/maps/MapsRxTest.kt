package com.google.maps.android.rx.maps

import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.rx.cameraIdleEvents
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.rxjava3.observers.TestObserver
import org.junit.Ignore
import org.junit.Test

class MapsRxTest {

    @Ignore("Blocked by GoogleMap final class mocking issues")
    @Test
    fun `cameraIdleEvents emits when listener is invoked`() {
        val googleMap: GoogleMap = mockk(relaxed = true)
        val listenerSlot = slot<GoogleMap.OnCameraIdleListener>()
        
        // Capture the listener
        every { googleMap.setOnCameraIdleListener(capture(listenerSlot)) } returns Unit

        val observer = TestObserver<Unit>()
        googleMap.cameraIdleEvents().subscribe(observer)

        // Verify listener was set
        verify { googleMap.setOnCameraIdleListener(any()) }
        
        // Trigger event
        if (listenerSlot.isCaptured) {
            listenerSlot.captured.onCameraIdle()
        }
        
        observer.assertValueCount(1)
        observer.assertNoErrors()
    }
}
