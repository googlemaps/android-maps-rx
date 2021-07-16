package com.google.maps.android.rx.places

import androidx.annotation.RequiresPermission
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.rx.places.internal.MainThreadTaskSingle
import com.google.maps.android.rx.places.internal.TaskCompletionListener
import io.reactivex.rxjava3.core.Single

/**
 * Finds the current place and emits it in a [Single].
 *
 * @param placeFields the fields to return for the retrieved Place
 * @param actions actions to be applied to the [FindCurrentPlaceRequest.Builder]
 * @return the Single emitting the current place
 */
@RequiresPermission(allOf = ["android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_WIFI_STATE"])
public fun PlacesClient.findCurrentPlace(
    placeFields: List<Place.Field>,
    actions: FindCurrentPlaceRequest.Builder.() -> Unit
): Single<FindCurrentPlaceResponse> =
    PlacesClientFindCurrentPlaceSingle(
        placesClient = this,
        placeFields = placeFields,
        actions = actions
    )

private class PlacesClientFindCurrentPlaceSingle(
    private val placesClient: PlacesClient,
    private val placeFields: List<Place.Field>,
    private val actions: FindCurrentPlaceRequest.Builder.() -> Unit
) : MainThreadTaskSingle<FindCurrentPlaceResponse>() {
    @RequiresPermission(allOf = ["android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_WIFI_STATE"])
    override fun invokeRequest(listener: TaskCompletionListener<FindCurrentPlaceResponse>) {
        val request = FindCurrentPlaceRequest.builder(placeFields)
            .apply(actions)
            .setCancellationToken(listener.cancellationTokenSource.token)
            .build()
        placesClient.findCurrentPlace(request).addOnCompleteListener(listener)
    }
}