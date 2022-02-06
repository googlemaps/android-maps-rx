![Tests](https://github.com/googlemaps/android-maps-rx/actions/workflows/test.yml/badge.svg)
![Beta](https://img.shields.io/badge/stability-beta-yellow)
[![Discord](https://img.shields.io/discord/676948200904589322)](https://discord.gg/hYsWbmk)
![Apache-2.0](https://img.shields.io/badge/license-Apache-blue)

Maps Android Rx
================

## Description
This repository contains RxJava bindings for the [Maps SDK for Android](maps-sdk) and [Places SDK for Android](places-sdk)

## Requirements
* API level 24+

## Installation

```groovy
dependencies {
    // RxJava bindings for the Maps SDK
    implementation 'com.google.maps.android:maps-rx:0.2.0'

    // RxJava bindings for the Places SDK
    implementation 'com.google.maps.android:places-rx:0.2.0'

    // It is recommended to also include the latest Maps SDK, Places SDK and RxJava so you
    // have the latest features and bug fixes.
    implementation 'com.google.android.gms:play-services-maps:<insert-latest-version>'
    implementation 'com.google.android.libraries.places:places:<insert-latest-version>'
    implementation 'io.reactivex.rxjava3:rxjava:<insert-latest-version>'
}
```

## Example Usage

### Marker Clicks (Maps)

```kotlin
val googleMap = // ...
googleMap.markerClicks()
  .subscribe { marker ->
    Log.d("DEBUG", "Marker ${marker.title} was clicked")
  }
```

### Combining camera events (Maps)

```kotlin
val googleMap = // ...
merge(
    googleMap.cameraIdleEvents(),
    googleMap.cameraMoveEvents(),
    googleMap.cameraMoveCanceledEvents(),
    googleMap.cameraMoveStartedEvents()
).subscribe {
    // Notified when one of the events triggered here
}
```

### Fetching a Place (Places)

```kotlin
val placesClient = // ...
placesClient.fetchPlace(
    placeId = "thePlaceId",
    placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)
).subscribe {
    // Notified when fetch completes/fails
}
```

## Documentation

You can learn more about all the extensions provided by this library by reading the [reference documents][Javadoc].

## Support

Encounter an issue while using this library?

If you find a bug or have a feature request, please [file an issue].
Or, if you'd like to contribute, send us a [pull request] and refer to our [code of conduct].

You can also reach us on our [Discord channel].

For more information, check out the detailed guide on the
[Google Developers site][devsite-guide]. 

[Discord channel]: https://discord.gg/hYsWbmk
[Javadoc]: https://googlemaps.github.io/android-maps-rx
[code of conduct]: CODE_OF_CONDUCT.md
[devsite-guide]: https://developers.google.com/maps/documentation/android-api/utility/
[file an issue]: https://github.com/googlemaps/android-maps-rx/issues/new/choose
[maps-sdk]: https://developers.google.com/maps/documentation/android-sdk/intro
[places-sdk]: https://developers.google.com/maps/documentation/places/android-sdk/overview
[pull request]: https://github.com/googlemaps/android-maps-rx/compare
