package com.google.maps.android.rx.internal

import kotlin.IllegalStateException

/**
 * Exception for when an action does not occur on the Android main thread when it should.
 */
public class NotOnMainThreadException : IllegalStateException(
    "Subscription is on ${Thread.currentThread().name} but must be on main thread."
)
