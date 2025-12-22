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

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.*
import com.google.android.gms.maps.MapView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.rx.cameraIdleEvents
import com.google.maps.android.rx.places.fetchPhoto
import com.google.maps.android.rx.places.findCurrentPlace
import com.trello.lifecycle4.android.lifecycle.AndroidLifecycle
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val provider = AndroidLifecycle.createLifecycleProvider(this)
    private lateinit var placesClient: PlacesClient
    private lateinit var mapView: MapView

    // Register the permission result callback
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            enableMyLocation()
        } else {
            Toast.makeText(this, "Permissions required for Places demo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Inset Handling: Decor fits system windows = false
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 2 & 3. Visibility & Behavior: Hide bars, transient swipe behavior
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

        // 3. Cutout Support: Short edges
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        // 4. Inset Handling: Apply insets to controls
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.controls_container)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, bars.bottom + v.paddingBottom)
            insets
        }
        
        // Initialize Places (provide a valid API key in local.defaults.properties or secrets)
        // Note: SDK must be initialized. Usually done in Application class, 
        // but here confirming if it's initialized or doing it here if simple.
        // Assuming Places is initialized in application or we do it here if needed.
        // For this demo, let's assume Application class initializes it or we add a check.
        // Actually, we should check if Places is initialized.
        if (!Places.isInitialized()) {
             try {
                val appInfo = packageManager.getApplicationInfo(packageName, android.content.pm.PackageManager.GET_META_DATA)
                val apiKey = appInfo.metaData?.getString("com.google.android.geo.API_KEY") ?: ""
                if (apiKey.isNotEmpty()) {
                    Places.initialize(applicationContext, apiKey)
                }
             } catch (e: Exception) {
                 Log.e(TAG, "Places not initialized", e)
             }
        }
        
        placesClient = Places.createClient(this)
        mapView = findViewById(R.id.mapView)

        val mapViewBundle = savedInstanceState?.getBundle(MAPVIEW_BUNDLE_KEY)
        mapView.onCreate(mapViewBundle)
        mapView.observe(lifecycle)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                val googleMap = mapView.awaitMap()
                
                // Set padding on Google Map to account for cutouts/bars
                // We can use the View's root insets to determine safe area
                ViewCompat.setOnApplyWindowInsetsListener(mapView) { _, insets ->
                     val bars = insets.getInsets(WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.systemBars())
                     googleMap.setPadding(bars.left, bars.top, bars.right, bars.bottom) // Ensure map controls aren't hidden
                     insets
                }
                
                // Trigger an inset pass to ensure padding is applied if map is ready after layout
                mapView.requestApplyInsets()
                
                // Camera events demo
                googleMap.cameraIdleEvents()
                    .compose(provider.bindToLifecycle())
                    .subscribe {
                        Log.d(TAG, "Camera is idle")
                    }
                    
                // Enable location if permission granted
                checkPermissions()
            }
        }

        findViewById<Button>(R.id.btnFindPlace).setOnClickListener {
            findCurrentPlace()
        }

        findViewById<Button>(R.id.btnFetchPhoto).setOnClickListener {
            // Demo: Fetch photo for a known place or the first result of current place
            fetchPhotoDemo()
        }
    }
    
    private fun checkPermissions() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_WIFI_STATE)
            )
        }
    }

    @Suppress("MissingPermission")
    private fun enableMyLocation() {
        lifecycleScope.launch {
            val map = mapView.awaitMap()
            map.isMyLocationEnabled = true
        }
    }

    @Suppress("MissingPermission")
    private fun findCurrentPlace() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            checkPermissions()
            return
        }

        val fields = listOf(Place.Field.DISPLAY_NAME, Place.Field.ID, Place.Field.PHOTO_METADATAS)
        placesClient.findCurrentPlace(fields)
            .compose(provider.bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                val place = response.placeLikelihoods.firstOrNull()?.place
                place?.let {
                    val message = "You are at ${it.displayName} (ID: ${it.id})"
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, message)
                } ?: run {
                    Toast.makeText(this, "No place found", Toast.LENGTH_SHORT).show()
                }
            }, { error ->
                Log.e(TAG, "Error finding place", error)
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            })
    }

    private fun fetchPhotoDemo() {
        // Hardcoded example or better logic?
        // Let's use San Francisco City Hall for demo if checkPermission fails or to be deterministic?
        // OR reuse the result from findCurrentPlace if available.
        // For simplicity, let's try to fetch photo for "San Francisco" place ID if we don't have a current place logic easily without async chain.
        // Actually, let's chain it: Find current place -> Get Photo.
        
        Toast.makeText(this, "Fetching photo for current place...", Toast.LENGTH_SHORT).show()
        
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
             checkPermissions()
             return
        }

        val fields = listOf(Place.Field.PHOTO_METADATAS)
        placesClient.findCurrentPlace(fields)
             .flatMap { response ->
                 val photoMetadata = response.placeLikelihoods.firstOrNull()?.place?.photoMetadatas?.firstOrNull()
                 if (photoMetadata != null) {
                     placesClient.fetchPhoto(photoMetadata)
                 } else {
                     io.reactivex.rxjava3.core.Single.error(Exception("No photos found"))
                 }
             }
             .compose(provider.bindToLifecycle())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe({ response ->
                 val message = "Photo fetched! Size: ${response.bitmap.width}x${response.bitmap.height}"
                 Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                 Log.d(TAG, message)
                 // Ideally show the bitmap in a dialog
             }, { error ->
                 val msg = "Error fetching photo: ${error.message}"
                 Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                 Log.e(TAG, msg)
             })
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
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            this@observe.onStart()
        }

        override fun onResume(owner: LifecycleOwner) {
            this@observe.onResume()
        }

        override fun onPause(owner: LifecycleOwner) {
            this@observe.onPause()
        }

        override fun onStop(owner: LifecycleOwner) {
            this@observe.onStop()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            this@observe.onDestroy()
        }
    })
}