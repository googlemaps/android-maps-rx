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

import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.rx.places.internal.MainThreadTaskSingle
import com.google.maps.android.rx.places.internal.TaskCompletionListener
import io.reactivex.rxjava3.core.Single

/**
 * Fetches a photo and emits the result in a [Single].
 *
 * @param photoMetadata the metadata for the photo to fetch
 * @param actions additional actions to apply to the [FetchPhotoRequest.Builder]
 * @return a [Single] emitting the response
 */
public fun PlacesClient.fetchPhoto(
    photoMetadata: PhotoMetadata,
    actions: FetchPhotoRequest.Builder.() -> Unit = {}
): Single<FetchPhotoResponse> =
    PlacesClientFetchPhotoSingle(
        placesClient = this,
        photoMetadata = photoMetadata,
        actions = actions
    )

private class PlacesClientFetchPhotoSingle(
    private val placesClient: PlacesClient,
    private val photoMetadata: PhotoMetadata,
    private val actions: FetchPhotoRequest.Builder.() -> Unit
) : MainThreadTaskSingle<FetchPhotoResponse>() {
    override fun invokeRequest(listener: TaskCompletionListener<FetchPhotoResponse>) {
        val request = FetchPhotoRequest.builder(photoMetadata)
            .apply(actions)
            .setCancellationToken(listener.cancellationTokenSource.token)
            .build()
        placesClient.fetchPhoto(request).addOnCompleteListener(listener)
    }
}