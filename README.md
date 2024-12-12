[![Maven Central](https://img.shields.io/maven-central/v/com.google.maps.android/maps-rx)](https://maven-badges.herokuapp.com/maven-central/com.google.maps.android/maps-rx)

![Beta](https://img.shields.io/badge/stability-beta-yellow)
![Release](https://github.com/googlemaps/android-maps-rx/workflows/Release/badge.svg)
[![Tests/Build Status](https://github.com/googlemaps/android-maps-rx/actions/workflows/test.yml/badge.svg)](https://github.com/googlemaps/android-maps-rx/actions/workflows/test.yml)

![GitHub contributors](https://img.shields.io/github/contributors/googlemaps/android-maps-rx?color=green)
[![GitHub License](https://img.shields.io/github/license/googlemaps/android-maps-rx?color=blue)][license]
[![Discord](https://img.shields.io/discord/676948200904589322?color=6A7EC2&logo=discord&logoColor=ffffff)][Discord server]

# Maps Android Rx

## Description

This repository contains RxJava bindings for the [Maps SDK for Android](maps-sdk) and [Places SDK for Android](places-sdk).

## Requirements
* Android API level 24+
* [Sign up with Google Maps Platform]
* A Google Maps Platform [project] with the **Maps SDK for Android** enabled
* An [API key] associated with the project above

## Installation

```groovy
dependencies {
    // RxJava bindings for the Maps SDK
    implementation 'com.google.maps.android:maps-rx:1.0.0'

    // RxJava bindings for the Places SDK
    implementation 'com.google.maps.android:places-rx:1.0.0'

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

See the [documentation] for a full list of classes and their methods.

Full guides for using the utilities are published in
[Google Maps Platform documentation](https://developers.google.com/maps/documentation/android-sdk/utility).

## Contributing

Contributions are welcome and encouraged! If you'd like to contribute, send us a [pull request] and refer to our [code of conduct] and [contributing guide].

## Terms of Service

This library uses Google Maps Platform services. Use of Google Maps Platform services through this library is subject to the Google Maps Platform [Terms of Service].

This library is not a Google Maps Platform Core Service. Therefore, the Google Maps Platform Terms of Service (e.g. Technical Support Services, Service Level Agreements, and Deprecation Policy) do not apply to the code in this library.

## Support

This library is offered via an open source [license]. It is not governed by the Google Maps Platform Support [Technical Support Services Guidelines, the SLA, or the [Deprecation Policy]. However, any Google Maps Platform services used by the library remain subject to the Google Maps Platform Terms of Service.

This library adheres to [semantic versioning] to indicate when backwards-incompatible changes are introduced. Accordingly, while the library is in version 0.x, backwards-incompatible changes may be introduced at any time.

If you find a bug, or have a feature request, please [file an issue] on GitHub. If you would like to get answers to technical questions from other Google Maps Platform developers, ask through one of our [developer community channels]. If you'd like to contribute, please check the [contributing guide].

You can also discuss this library on our [Discord server].

[API key]: https://developers.google.com/maps/documentation/android-sdk/get-api-key
[gmp-start]: https://console.cloud.google.com/google/maps-apis/start
[maps-sdk]: https://developers.google.com/maps/documentation/android-sdk
[places-sdk]: https://developers.google.com/maps/documentation/places/android-sdk
[documentation]: https://googlemaps.github.io/android-maps-rx
[jetpack-rx]: https://developer.android.com/jetpack/compose

[code of conduct]: CODE_OF_CONDUCT.md
[contributing guide]: CONTRIBUTING.md
[Deprecation Policy]: https://cloud.google.com/maps-platform/terms
[developer community channels]: https://developers.google.com/maps/developer-community
[Discord server]: https://discord.gg/hYsWbmk
[file an issue]: https://github.com/googlemaps/android-maps-rx/issues/new/choose
[license]: LICENSE
[project]: https://developers.google.com/maps/documentation/android-sdk/cloud-setup
[pull request]: https://github.com/googlemaps/android-maps-rx/compare
[semantic versioning]: https://semver.org
[Sign up with Google Maps Platform]: https://console.cloud.google.com/google/maps-apis/start
[similar inquiry]: https://github.com/googlemaps/android-maps-rx/issues
[SLA]: https://cloud.google.com/maps-platform/terms/sla
[Technical Support Services Guidelines]: https://cloud.google.com/maps-platform/terms/tssg
[Terms of Service]: https://cloud.google.com/maps-platform/terms
