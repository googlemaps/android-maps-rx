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

import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.rx.places.internal.MainThreadTaskSingle
import com.google.maps.android.rx.places.internal.TaskCompletionListener
import io.reactivex.rxjava3.core.Single

/**
 * Finds autocomplete predictions and emits the results in a [Single].
 *
 * @param actions actions to be applied to the [FindAutocompletePredictionsRequest.Builder]
 * @return a [Single] emitting the response
 */
public fun PlacesClient.findAutocompletePrediction(
    actions: FindAutocompletePredictionsRequest.Builder.() -> Unit
): Single<FindAutocompletePredictionsResponse> =
    PlacesClientFindAutocompletePredictionsSingle(this, actions)

private class PlacesClientFindAutocompletePredictionsSingle(
    private val placesClient: PlacesClient,
    private val actions: FindAutocompletePredictionsRequest.Builder.() -> Unit
) : MainThreadTaskSingle<FindAutocompletePredictionsResponse>() {
    override fun invokeRequest(listener: TaskCompletionListener<FindAutocompletePredictionsResponse>) {
        val request = FindAutocompletePredictionsRequest.builder()
            .apply(actions)
            .setCancellationToken(listener.cancellationTokenSource.token)
            .build()
        placesClient.findAutocompletePredictions(request).addOnCompleteListener(listener)
    }
}