package com.example.vkr.activity.maps

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.vkr.R
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.ui_view.ViewProvider


class YandexMapsActivity : AppCompatActivity() {

    private lateinit var mapview : MapView
    private val MAPKIT_API_KEY = "c516b242-0d62-453a-8e44-2c9002475cba"

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
        setContentView(R.layout.yandex_maps_activity)
        super.onCreate(savedInstanceState)

        mapview = findViewById(R.id.mapView)
        mapview.map.isRotateGesturesEnabled = false
        mapview.map.move(
            CameraPosition(Point(53.213779, 50.176837), 16f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0F),
            null)
        drawMyLocationMark(Point(53.213782, 50.176837))
    }

    override fun onStart() {
        mapview.onStart()
        MapKitFactory.getInstance().onStart()
        super.onStart()
    }

    override fun onStop() {
        mapview.onStop()
        MapKitFactory.getInstance().onStart()
        super.onStop()
    }
    private fun drawMyLocationMark(it: Point) {
        val view = View(applicationContext).apply {
            background = applicationContext.getDrawable(R.drawable.ic_baseline_place_24)
        }

        mapview.map.mapObjects.addPlacemark(
            it,
            ViewProvider(view)
        )
    }
}