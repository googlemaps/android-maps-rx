// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.maps.android.rx.demo

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.google.android.gms.maps.MapView
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.rx.cameraIdleEvents
import com.trello.lifecycle4.android.lifecycle.AndroidLifecycle

class MainActivity : AppCompatActivity() {

    private val provider = AndroidLifecycle.createLifecycleProvider(this)

    private val mapView by lazy {
        MapView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mapView)
        val mapViewBundle = savedInstanceState?.getBundle(MAPVIEW_BUNDLE_KEY)
        mapView.onCreate(mapViewBundle)
        mapView.observe(lifecycle)

        lifecycle.coroutineScope.launchWhenCreated {
            val googleMap = mapView.awaitMap()
            googleMap.cameraIdleEvents()
                .compose(provider.bindToLifecycle())
                .subscribe {
                    Log.d(TAG, "Camera is idle")
                }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY) ?: Bundle().also {
            outState.putBundle(MAPVIEW_BUNDLE_KEY, it)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
        private val TAG = MainActivity::class.java.simpleName
    }
}

fun MapView.observe(lifecycle: Lifecycle) {
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            this@observe.onStart()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            this@observe.onResume()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            this@observe.onPause()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            this@observe.onStop()
        }
    })
}