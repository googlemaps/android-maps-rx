package com.google.maps.android.rx.places

import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.rx.places.internal.MainThreadTaskSingle
import com.google.maps.android.rx.places.internal.TaskCompletionListener
import io.reactivex.rxjava3.core.Single

/**
 * Fetches a [Place] and emits the result in a [Single].
 *
 * @param placeId the ID of the place to be requested
 * @param placeFields the fields of the place to be requested
 * @param actions additional actions to apply to the [FetchPlaceRequest.Builder]
 * @return a [Single] emitting the response
 */
public fun PlacesClient.fetchPlace(
    placeId: String,
    placeFields: List<Place.Field>,
    actions: FetchPlaceRequest.Builder.() -> Unit = {}
): Single<FetchPlaceResponse> =
    PlacesClientFetchPlaceSingle(
        placesClient = this,
        placeId = placeId,
        placeFields = placeFields,
        actions = actions
    )

private class PlacesClientFetchPlaceSingle(
    private val placesClient: PlacesClient,
    private val placeId: String,
    private val placeFields: List<Place.Field>,
    private val actions: FetchPlaceRequest.Builder.() -> Unit
) : MainThreadTaskSingle<FetchPlaceResponse>() {
    override fun invokeRequest(listener: TaskCompletionListener<FetchPlaceResponse>) {
        val request = FetchPlaceRequest.builder(placeId, placeFields)
            .apply(actions)
            .setCancellationToken(listener.cancellationTokenSource.token)
            .build()
        placesClient.fetchPlace(request).addOnCompleteListener(listener)
    }
}