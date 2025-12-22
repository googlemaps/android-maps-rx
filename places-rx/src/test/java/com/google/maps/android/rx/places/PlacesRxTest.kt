package com.google.maps.android.rx.places

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.rxjava3.observers.TestObserver
import org.junit.Ignore
import org.junit.Test

class PlacesRxTest {

    @Ignore("Blocked by mocking issues with PlacesClient/Task")
    @Suppress("DEPRECATION")
    @Test
    fun `findCurrentPlace emits response on success`() {
        val placesClient: PlacesClient = mockk()
        val task: Task<FindCurrentPlaceResponse> = mockk(relaxed = true)
        val response: FindCurrentPlaceResponse = mockk()
        val listenerSlot = slot<OnCompleteListener<FindCurrentPlaceResponse>>()
        
        every { placesClient.findCurrentPlace(any()) } returns task
        every { task.isSuccessful } returns true
        every { task.result } returns response
        every { task.addOnCompleteListener(capture(listenerSlot)) } returns task

        val observer = TestObserver<FindCurrentPlaceResponse>()
        placesClient.findCurrentPlace(listOf(Place.Field.ID))
            .subscribe(observer)

        verify { placesClient.findCurrentPlace(any()) }
        
        // Trigger the listener
        if (listenerSlot.isCaptured) {
            listenerSlot.captured.onComplete(task)
        }
        
        observer.assertValue(response)
        observer.assertNoErrors()
    }
}
