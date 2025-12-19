/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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